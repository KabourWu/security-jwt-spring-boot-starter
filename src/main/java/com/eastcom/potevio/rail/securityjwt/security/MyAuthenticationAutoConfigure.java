package com.eastcom.potevio.rail.securityjwt.security;

import com.eastcom.potevio.rail.securityjwt.security.auth.ajax.AjaxAwareAuthenticationFailureHandler;
import com.eastcom.potevio.rail.securityjwt.security.auth.ajax.AjaxAwareAuthenticationSuccessHandler;
import com.eastcom.potevio.rail.securityjwt.security.auth.jwt.JwtAuthenticationProvider;
import com.eastcom.potevio.rail.securityjwt.security.authority.ApiAuthorityAnnotationAdvisor;
import com.eastcom.potevio.rail.securityjwt.security.authority.ApiAuthorityBeanPostProcessor;
import com.eastcom.potevio.rail.securityjwt.security.authority.ApiAuthorityMethodInterceptor;
import com.eastcom.potevio.rail.securityjwt.security.config.AjaxSettings;
import com.eastcom.potevio.rail.securityjwt.security.config.JwtAndAjaxWebSecurityConfig;
import com.eastcom.potevio.rail.securityjwt.security.config.JwtSettings;
import com.eastcom.potevio.rail.securityjwt.security.config.JwtWebSecurityConfig;
import org.springframework.aop.Advisor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.client.RestTemplate;

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
	RestTemplate restTemplate() {
		return new RestTemplate();
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
