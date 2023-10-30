package com.eastcom.potevio.rail.securityjwt.security.endpoint;

import com.eastcom.potevio.rail.securityjwt.common.ErrorCode;
import com.eastcom.potevio.rail.securityjwt.common.ResultResponse;
import com.eastcom.potevio.rail.securityjwt.security.JwtTokenHelper;
import com.eastcom.potevio.rail.securityjwt.security.auth.ajax.LoginRequest;
import com.eastcom.potevio.rail.securityjwt.security.auth.ajax.service.MyAdminDetailsService;
import com.eastcom.potevio.rail.securityjwt.security.auth.ajax.service.MyMemberDetailsService;
import com.eastcom.potevio.rail.securityjwt.security.auth.ajax.service.MyUserDetailsService;
import com.eastcom.potevio.rail.securityjwt.security.auth.ajax.tokenmanager.refreshtoken.RefreshTokenManager;
import com.eastcom.potevio.rail.securityjwt.security.config.JwtSettings;
import com.eastcom.potevio.rail.securityjwt.security.config.JwtWebSecurityConfig;
import com.eastcom.potevio.rail.securityjwt.security.exceptions.InvalidRefreshTokenException;
import com.eastcom.potevio.rail.securityjwt.security.model.MyUserDetails;
import com.eastcom.potevio.rail.securityjwt.security.model.UserContext;
import com.eastcom.potevio.rail.securityjwt.security.model.token.JwtToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author kabour
 * @date 2019/6/19
 */
@RestController
@ConditionalOnProperty(name = "security.ajax.enabled", havingValue = "true")
public class RefreshTokenEndpoint {
    @Autowired
    private JwtTokenHelper jwtTokenHelper;
    @Autowired
    private RefreshTokenManager refreshTokenManager;
    @Autowired
    private MyMemberDetailsService myMemberDetailsService;
    @Autowired
    private MyAdminDetailsService myAdminDetailsService;
    @Autowired
    private JwtSettings jwtSettings;

    @PutMapping(value = JwtWebSecurityConfig.REFRESH_TOKEN_URL, produces = {MediaType.APPLICATION_JSON_VALUE})
    public Map<String, String> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtTokenHelper.extract(request.getHeader(JwtWebSecurityConfig.AUTHENTICATION_HEADER_NAME));

        Jws<Claims> jws = jwtTokenHelper.parseRefreshToken(accessToken);

        LoginRequest.UserType type = jwtTokenHelper.userType(jws);
        MyUserDetailsService myUserDetailsService;
        if (type == LoginRequest.UserType.ADMIN) {
            myUserDetailsService = myAdminDetailsService;
        } else {
            myUserDetailsService = myMemberDetailsService;
        }

        String subject = jws.getBody().getSubject();
        MyUserDetails userDetails = myUserDetailsService.loadUserByUsername(subject);
        if (userDetails == null) throw new UsernameNotFoundException("User not found: " + subject);

        Collection authorities = userDetails.getAuthorities();
        if (authorities == null)
            throw new InsufficientAuthenticationException("MyUserDetails has no roles assigned");
        UserContext userContext = new UserContext(userDetails.getUsername(), jwtTokenHelper.userType(jws), jwtTokenHelper.agentType(jws), userDetails.getUserBasicDetail(), authorities);
        //检查 用户是否加入黑名单
        if (!refreshTokenManager.verify(subject, userContext)) {
            throw new InvalidRefreshTokenException();
        }
        //是否重用 refresh token， 默认重用
        if (jwtSettings.getRefreshTokenReuse() == null || jwtSettings.getRefreshTokenReuse()) {
            JwtToken jwtToken = jwtTokenHelper.createAccessJwtToken(userContext);
            Map<String, String> tokenMap = new LinkedHashMap<>(4);
            tokenMap.put("token", jwtToken.getToken());
            tokenMap.put("refreshToken", accessToken);
            return tokenMap;
        }

        return jwtTokenHelper.createTokenPair(userContext);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    ResultResponse exceptionHandler(Exception e) {
        return ResultResponse.failed(e.getMessage(), ErrorCode.INVALID_REFRESH_TOKEN);
    }
}
