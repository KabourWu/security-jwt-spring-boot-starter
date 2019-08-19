package online.kabour.springbootbase.securityjwt.security.auth.ajax;

import com.alibaba.fastjson.JSONObject;
import online.kabour.springbootbase.securityjwt.security.model.UserContext;
import online.kabour.springbootbase.securityjwt.security.JwtTokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * @author kabour
 * @date 2019/6/19
 */
public class AjaxAwareAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	private final JwtTokenHelper jwtTokenHelper;

	@Autowired
	public AjaxAwareAuthenticationSuccessHandler(final JwtTokenHelper jwtTokenHelper) {
		this.jwtTokenHelper = jwtTokenHelper;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	                                    Authentication authentication) throws IOException, ServletException {
		UserContext userContext = (UserContext) authentication.getPrincipal();
		Map<String, String> tokenMap = jwtTokenHelper.createTokenPair(userContext);

		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().write(JSONObject.toJSONString(tokenMap));

		clearAuthenticationAttributes(request);
	}

	/**
	 * Removes temporary authentication-related data which may have been stored
	 * in the session during the authentication process..
	 */
	protected final void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		if (session == null) {
			return;
		}

		session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	}
}
