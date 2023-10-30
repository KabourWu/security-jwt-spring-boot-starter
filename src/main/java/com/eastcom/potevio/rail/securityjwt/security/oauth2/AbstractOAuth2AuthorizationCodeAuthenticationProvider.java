package com.eastcom.potevio.rail.securityjwt.security.oauth2;


import com.alibaba.fastjson.JSONObject;
import com.eastcom.potevio.rail.securityjwt.security.auth.ajax.LoginRequest;
import com.eastcom.potevio.rail.securityjwt.security.auth.ajax.service.MyUserDetailsService;
import com.eastcom.potevio.rail.securityjwt.security.config.AjaxAuthenticationConfig;
import com.eastcom.potevio.rail.securityjwt.security.model.MyUserDetails;
import com.eastcom.potevio.rail.securityjwt.security.model.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static com.eastcom.potevio.rail.securityjwt.security.oauth2.OAuth2ClientProperties.KEY_AUTH2;


/**
 * 单点登录认证provider
 *
 * @author kabour
 * @since 2023/10/27 10:55
 */
public abstract class AbstractOAuth2AuthorizationCodeAuthenticationProvider implements AuthenticationProvider {


    protected final OAuth2ClientProperties oAuth2ClientProperties;

    protected final RestTemplate restTemplate;

    protected final AjaxAuthenticationConfig ajaxAuthenticationConfig;

    public AbstractOAuth2AuthorizationCodeAuthenticationProvider(OAuth2ClientProperties oAuth2ClientProperties, RestTemplate restTemplate, AjaxAuthenticationConfig ajaxAuthenticationConfig) {
        this.oAuth2ClientProperties = oAuth2ClientProperties;
        this.restTemplate = restTemplate;
        this.ajaxAuthenticationConfig = ajaxAuthenticationConfig;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        String code = oAuth2AuthenticationToken.getCode(), state = oAuth2AuthenticationToken.getState();
        String providerName = oAuth2ClientProperties.getRegistration().get(KEY_AUTH2).getProvider();
        OAuth2ClientProperties.Provider providerObj = oAuth2ClientProperties.getProvider().get(providerName);
        OAuth2AccessTokenResponse tokenObj = codeToToken(providerName, code);
        if (tokenObj == null) {
            throw new AuthenticationServiceException("获取访问凭证失败");
        }
        JSONObject userInfo = getUserInfo(providerName, tokenObj.getAccessToken());
        if (userInfo == null) {
            throw new AuthenticationServiceException("获取用户信息失败");
        }
        String username = (String) userInfo.get(providerObj.getUserNameAttribute());
        if (StringUtils.isBlank(username)) {
            throw new AuthenticationServiceException("获取用户信息失败：" + username);
        }
        if (providerObj.isUserLocalCheck()) {
            MyUserDetailsService userDetailsService;
            LoginRequest.UserType userType;
            if (LoginRequest.UserType.ADMIN.name().equalsIgnoreCase(state)) {
                userDetailsService = ajaxAuthenticationConfig.getAdminDetailsService();
                userType = LoginRequest.UserType.ADMIN;
            } else {
                userDetailsService = ajaxAuthenticationConfig.getMemberDetailsService();
                userType = LoginRequest.UserType.MEMBER;
            }
            MyUserDetails user = userDetailsService.loadUserByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("用户名或密码错误");
            }
            oAuth2AuthenticationToken.setDetails(user.getAuthorities());
            LoginRequest loginRequest = new LoginRequest(username, null, null, userType, LoginRequest.AgentType.WEB);
            UserContext userContext = UserContext.create(user.getUsername(), loginRequest, user.getUserBasicDetail(), user.getAuthorities());
            oAuth2AuthenticationToken.setPrincipal(userContext);
        } else {
            oAuth2AuthenticationToken.setDetails(Collections.emptySet());
            UserContext.UserBasicDetail userBasicDetail = new UserContext.UserBasicDetail(username, username, null);
            LoginRequest loginRequest = new LoginRequest(username, null, null, LoginRequest.UserType.ADMIN, LoginRequest.AgentType.WEB);
            UserContext userContext = UserContext.create(username, loginRequest, userBasicDetail, Collections.emptySet());
            oAuth2AuthenticationToken.setPrincipal(userContext);
        }
        oAuth2AuthenticationToken.setAuthenticated(true);
        return oAuth2AuthenticationToken;
    }

    /**
     * 授权码换取令牌
     *
     * @param provider 认证provider
     * @param code     授权码
     * @return 令牌实体
     */
    public abstract OAuth2AccessTokenResponse codeToToken(String provider, String code);

    /**
     * 获取用户信息
     *
     * @param provider 认证provider
     * @param token    令牌
     * @return 获取用户信息
     */
    public abstract JSONObject getUserInfo(String provider, String token);

    @Override
    public boolean supports(Class<?> aClass) {
        return OAuth2AuthenticationToken.class.equals(aClass);
    }
}
