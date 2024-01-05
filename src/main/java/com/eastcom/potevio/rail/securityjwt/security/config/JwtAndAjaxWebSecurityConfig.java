package com.eastcom.potevio.rail.securityjwt.security.config;

import com.eastcom.potevio.rail.securityjwt.security.CaptchaVerify;
import com.eastcom.potevio.rail.securityjwt.security.CustomCorsFilter;
import com.eastcom.potevio.rail.securityjwt.security.MyAccessDeniedHandler;
import com.eastcom.potevio.rail.securityjwt.security.RestAuthenticationEntryPoint;
import com.eastcom.potevio.rail.securityjwt.security.auth.ajax.AjaxAuthenticationProvider;
import com.eastcom.potevio.rail.securityjwt.security.auth.ajax.AjaxLoginProcessingFilter;
import com.eastcom.potevio.rail.securityjwt.security.auth.ajax.tokenmanager.authtoken.AuthTokenManager;
import com.eastcom.potevio.rail.securityjwt.security.auth.ajax.tokenmanager.authtoken.RedisAuthTokenManager;
import com.eastcom.potevio.rail.securityjwt.security.auth.ajax.tokenmanager.refreshtoken.RedisRefreshTokenManager;
import com.eastcom.potevio.rail.securityjwt.security.auth.ajax.tokenmanager.refreshtoken.RefreshTokenManager;
import com.eastcom.potevio.rail.securityjwt.security.auth.jwt.JwtAuthenticationProvider;
import com.eastcom.potevio.rail.securityjwt.security.oauth2.AbstractOAuth2AuthorizationCodeAuthenticationProvider;
import com.eastcom.potevio.rail.securityjwt.security.oauth2.MyOAuth2LoginAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author kabour
 * @date 2019/6/19
 */
@ConditionalOnProperty(name = "security.ajax.enabled", havingValue = "true")
public class JwtAndAjaxWebSecurityConfig extends JwtWebSecurityConfig {

    @Autowired
    private AuthenticationSuccessHandler successHandler;
    @Autowired
    private AuthenticationFailureHandler failureHandler;
    @Autowired
    private AjaxAuthenticationProvider ajaxAuthenticationProvider;
    @Autowired
    private JwtAuthenticationProvider jwtAuthenticationProvider;
    @Autowired(required = false)
    private AbstractOAuth2AuthorizationCodeAuthenticationProvider oAuth2AuthorizationCodeAuthenticationProvider;

    protected AjaxLoginProcessingFilter buildAjaxLoginProcessingFilter(String loginEntryPoint) throws Exception {
        AjaxLoginProcessingFilter filter = new AjaxLoginProcessingFilter(loginEntryPoint, successHandler, failureHandler);
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }

    protected MyOAuth2LoginAuthenticationFilter buildMyOAuth2LoginAuthenticationFilter(String loginEntryPoint) throws Exception {
        MyOAuth2LoginAuthenticationFilter myOAuth2LoginAuthenticationFilter = new MyOAuth2LoginAuthenticationFilter(loginEntryPoint, successHandler, failureHandler);
        myOAuth2LoginAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
        return myOAuth2LoginAuthenticationFilter;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(ajaxAuthenticationProvider);
        auth.authenticationProvider(jwtAuthenticationProvider);
        if (oAuth2AuthorizationCodeAuthenticationProvider != null) {
            auth.authenticationProvider(oAuth2AuthorizationCodeAuthenticationProvider);
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        addPermitEndpoint(permitAllEndpointList);
        addMvcPermitEndpoint(annotationPermitAllEndpointList);

        CustomCorsFilter customCorsFilter = new CustomCorsFilter();
        http
                .csrf().disable() // We don't need CSRF for JWT based authentication
                .exceptionHandling()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .accessDeniedHandler(new MyAccessDeniedHandler())
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(buildAjaxLoginProcessingFilter(AUTHENTICATION_URL), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(buildJwtTokenAuthenticationProcessingFilter(permitAllEndpointList,
                        annotationPermitAllEndpointList, API_ROOT_URL), UsernamePasswordAuthenticationFilter.class);
        if (oAuth2AuthorizationCodeAuthenticationProvider != null) {
            http.addFilterBefore(buildMyOAuth2LoginAuthenticationFilter(AUTHENTICATION_URL), AjaxLoginProcessingFilter.class)
                    .addFilterBefore(customCorsFilter, MyOAuth2LoginAuthenticationFilter.class);
        } else {
            http.addFilterBefore(new CustomCorsFilter(), UsernamePasswordAuthenticationFilter.class);
        }
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    @ConditionalOnMissingBean(AjaxAuthenticationProvider.class)
    AjaxAuthenticationProvider ajaxAuthenticationProvider() {
        return new AjaxAuthenticationProvider();
    }

    @Bean
    @ConditionalOnMissingBean(CaptchaVerify.class)
    CaptchaVerify captchaVerify() {
        return (username, captcha) -> true;
    }

    @Bean
    @ConditionalOnMissingBean(AjaxAuthenticationConfig.class)
    AjaxAuthenticationConfig ajaxAuthenticationConfig() {
        return new DefaultAjaxAuthenticationConfig();
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.data.redis.connection.RedisConnectionFactory")
    RefreshTokenManager redisRefreshTokenManager() {
        return new RedisRefreshTokenManager();
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.data.redis.connection.RedisConnectionFactory")
    AuthTokenManager redisAuthTokenManager() {
        return new RedisAuthTokenManager();
    }

}
