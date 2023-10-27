package online.kabour.springbootbase.securityjwt.security;

import online.kabour.springbootbase.securityjwt.security.auth.ajax.AjaxAwareAuthenticationFailureHandler;
import online.kabour.springbootbase.securityjwt.security.auth.ajax.AjaxAwareAuthenticationSuccessHandler;
import online.kabour.springbootbase.securityjwt.security.auth.jwt.JwtAuthenticationProvider;
import online.kabour.springbootbase.securityjwt.security.authority.ApiAuthorityAnnotationAdvisor;
import online.kabour.springbootbase.securityjwt.security.authority.ApiAuthorityBeanPostProcessor;
import online.kabour.springbootbase.securityjwt.security.authority.ApiAuthorityMethodInterceptor;
import online.kabour.springbootbase.securityjwt.security.config.AjaxSettings;
import online.kabour.springbootbase.securityjwt.security.config.JwtAndAjaxWebSecurityConfig;
import online.kabour.springbootbase.securityjwt.security.config.JwtSettings;
import online.kabour.springbootbase.securityjwt.security.config.JwtWebSecurityConfig;
import org.springframework.aop.Advisor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * @author kabour
 * @date 2019/6/10 20:56
 */
@Configuration
@AutoConfigureAfter(name = "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration")
@EnableConfigurationProperties({JwtSettings.class, AjaxSettings.class})
@EnableWebSecurity
@ComponentScan(basePackageClasses = MyAccessDeniedHandler.class)
@Import({JwtWebSecurityConfig.class, JwtAndAjaxWebSecurityConfig.class})
public class MyAuthenticationAutoConfigure {

	@Bean
	ApiAuthorityBeanPostProcessor apiAuthorityBeanPostProcessor() {
		Advisor advisor = new ApiAuthorityAnnotationAdvisor(new ApiAuthorityMethodInterceptor());
		return new ApiAuthorityBeanPostProcessor(advisor);
	}

	@Bean
	JwtTokenHelper jwtTokenHelper() {
		return new JwtTokenHelper();
	}

	@Bean
	JwtAuthenticationProvider jwtAuthenticationProvider() {
		return new JwtAuthenticationProvider();
	}

	@Bean
	DefaultAuthorizationCodeTokenResponseClient defaultAuthorizationCodeTokenResponseClient() {
		return new DefaultAuthorizationCodeTokenResponseClient();
	}

	@Bean
	OAuth2AuthorizationCodeAuthenticationProvider oAuth2AuthorizationCodeAuthenticationProvider(OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient ) {
		return new OAuth2AuthorizationCodeAuthenticationProvider(accessTokenResponseClient);
	}

	@Bean
	@ConditionalOnMissingBean(AuthenticationSuccessHandler.class)
	AjaxAwareAuthenticationSuccessHandler ajaxAwareAuthenticationSuccessHandler(JwtTokenHelper jwtTokenHelper) {
		return new AjaxAwareAuthenticationSuccessHandler(jwtTokenHelper);
	}

	@Bean
	@ConditionalOnMissingBean(AuthenticationFailureHandler.class)
	AjaxAwareAuthenticationFailureHandler ajaxAwareAuthenticationFailureHandler() {
		return new AjaxAwareAuthenticationFailureHandler();
	}

}
