package online.kabour.springbootbase.securityjwt.security.auth.ajax;

import online.kabour.springbootbase.securityjwt.common.WebUtil;
import online.kabour.springbootbase.securityjwt.security.CaptchaVerify;
import online.kabour.springbootbase.securityjwt.security.auth.ajax.service.MyUserDetailsService;
import online.kabour.springbootbase.securityjwt.security.config.AjaxAuthenticationConfig;
import online.kabour.springbootbase.securityjwt.security.exceptions.CaptchaInvalidException;
import online.kabour.springbootbase.securityjwt.security.model.MyUserDetails;
import online.kabour.springbootbase.securityjwt.security.model.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author kabour
 * @date 2019/6/19
 */
public class AjaxAuthenticationProvider implements AuthenticationProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(AjaxAuthenticationProvider.class);

	@Autowired
	private BCryptPasswordEncoder encoder;

	@Autowired
	private AjaxAuthenticationConfig ajaxAuthenticationConfig;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Assert.notNull(authentication, "No authentication data provided");

		String username = (String) authentication.getPrincipal();
		String password = (String) authentication.getCredentials();
		LoginRequest loginRequest = (LoginRequest) authentication.getDetails();

		MyUserDetailsService userDetailsService;
		if (loginRequest.getType() == LoginRequest.UserType.ADMIN) {
			userDetailsService = ajaxAuthenticationConfig.getAdminDetailsService();
		} else {
			userDetailsService = ajaxAuthenticationConfig.getMemberDetailsService();
		}

		MyUserDetails user = userDetailsService.loadMyUserDetailsByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found: " + username);
		}

		boolean isValid = verifyCaptcha(username, loginRequest.getCaptcha());
		if (!isValid) throw new CaptchaInvalidException("错误的验证码");

		preCheck(user, userDetailsService);
		if (!encoder.matches(password, user.getPassword())) {
			byte count = userDetailsService.myUserDetailsSerialTrialErrorCountPlusOne(username);
			if (count >= ajaxAuthenticationConfig.getSerialTrialCount()) {
				userDetailsService.updateMyUserDetailsLockedTime(username, new Date());
			}
			throw new BadCredentialsException("Authentication Failed. Username or Password not valid.");
		}
		postCheck(user, userDetailsService);

		userDetailsService.updateMyUserDetailsLoginTimeByUsername(username);
		UserContext userContext = UserContext.create(user.getUsername(), loginRequest, user.getUserBasicDetail(), user.getAuthorities());
		return new UsernamePasswordAuthenticationToken(userContext, null, userContext.getAuthorities());
	}

	/**
	 * 核对验证码
	 */
	private boolean verifyCaptcha(String username, String captcha) {
		CaptchaVerify captchaVerify = ajaxAuthenticationConfig.getCaptchaVerify();
		if (captchaVerify != null)
			return captchaVerify.verify(username, captcha);
		return true;
	}

	/**
	 * 检查账号状态是否正常
	 *
	 * @param user
	 */
	private void postCheck(MyUserDetails user, MyUserDetailsService userDetailsService) {
		if (!user.isAccountNonLocked()) {
			LOGGER.debug("User account is locked");
			throw new LockedException("User account is locked");
		}

		if (!user.isEnabled()) {
			LOGGER.debug("User account is disabled");
			throw new DisabledException("User is disabled");
		}

		if (!user.isAccountNonExpired()) {
			LOGGER.debug("User account is expired");
			throw new AccountExpiredException("User account has expired");
		}
		if (user.getSerialTrialCount() > 0) {
			userDetailsService.updateMyUserDetailsSerialTrialErrorCountByUsername(user.getUsername(), (byte) 0);
		}
		if (user.getLockedTime() != null) {
			userDetailsService.updateMyUserDetailsLockedTime(user.getUsername(), null);
		}
	}

	private void preCheck(MyUserDetails user, MyUserDetailsService userDetailsService) {
		if (user.getLockedTime() != null) {
			Date removalDate = WebUtil.dateCursor(user.getLockedTime(), ajaxAuthenticationConfig.getLockedDuration(), Calendar.HOUR);
			Date now = new Date();
			if (now.before(removalDate)) {
				throw new LockedException(String.format("连续出错次数太多，请%s后再试",
						WebUtil.durationTransferToChinese(removalDate.getTime() - now.getTime(), TimeUnit.MILLISECONDS)));
			} else {
				userDetailsService.updateMyUserDetailsLockedTime(user.getUsername(), null);
				userDetailsService.updateMyUserDetailsSerialTrialErrorCountByUsername(user.getUsername(), (byte) 0);
			}
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}
}
