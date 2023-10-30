package com.eastcom.potevio.rail.securityjwt.security.oauth2;

import com.eastcom.potevio.rail.securityjwt.security.auth.ajax.AjaxLoginProcessingFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义Oauth2认证成功和失败返回信息
 *
 * @author kabour
 * @since 2023/10/26 16:14
 */
public class MyOAuth2LoginAuthenticationFilter extends AjaxLoginProcessingFilter {

    public static final String PARAM_CODE = "code";

    public static final String PARAM_STATE = "state";


    public MyOAuth2LoginAuthenticationFilter(String defaultProcessUrl, AuthenticationSuccessHandler successHandler, AuthenticationFailureHandler failureHandler) {
        super(defaultProcessUrl, successHandler, failureHandler);
    }

    /**
     * /auth/token带了code和state参数的请求判定为sso登录请求
     *
     * @param request  http 请求
     * @param response http 响应
     * @return true: 进入sso登录认证
     */
    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        boolean requiresAuth = super.requiresAuthentication(request, response);
        if (!requiresAuth) {
            return false;
        }
        String code = request.getParameter(PARAM_CODE), state = request.getParameter(PARAM_STATE);
        return StringUtils.isNotBlank(code) && StringUtils.isNotBlank(state);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String code = request.getParameter(PARAM_CODE), state = request.getParameter(PARAM_STATE);
        if (StringUtils.isBlank(code)) {
            throw new AuthenticationServiceException("Invalid Authentication Code");
        }

        OAuth2AuthenticationToken authenticationRequest = new OAuth2AuthenticationToken(code, state);
        return this.getAuthenticationManager().authenticate(authenticationRequest);
    }
}
