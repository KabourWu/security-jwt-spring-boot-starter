package online.kabour.springbootbase.securityjwt.security.config;

import online.kabour.springbootbase.securityjwt.security.CaptchaVerify;
import online.kabour.springbootbase.securityjwt.security.CustomCorsFilter;
import online.kabour.springbootbase.securityjwt.security.MyAccessDeniedHandler;
import online.kabour.springbootbase.securityjwt.security.RestAuthenticationEntryPoint;
import online.kabour.springbootbase.securityjwt.security.auth.ajax.AjaxAuthenticationProvider;
import online.kabour.springbootbase.securityjwt.security.auth.ajax.AjaxLoginProcessingFilter;
import online.kabour.springbootbase.securityjwt.security.auth.ajax.tokenmanager.authtoken.AuthTokenManager;
import online.kabour.springbootbase.securityjwt.security.auth.ajax.tokenmanager.authtoken.RedisAuthTokenManager;
import online.kabour.springbootbase.securityjwt.security.auth.ajax.tokenmanager.refreshtoken.RedisRefreshTokenManager;
import online.kabour.springbootbase.securityjwt.security.auth.ajax.tokenmanager.refreshtoken.RefreshTokenManager;
import online.kabour.springbootbase.securityjwt.security.auth.jwt.JwtAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
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

	protected AjaxLoginProcessingFilter buildAjaxLoginProcessingFilter(String loginEntryPoint) throws Exception {
		AjaxLoginProcessingFilter filter = new AjaxLoginProcessingFilter(loginEntryPoint, successHandler, failureHandler);
		filter.setAuthenticationManager(authenticationManagerBean());
		return filter;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(ajaxAuthenticationProvider);
		auth.authenticationProvider(jwtAuthenticationProvider);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		addPermitEndpoint(permitAllEndpointList);
		addMvcPermitEndpoint(annotationPermitAllEndpointList);

		http
				.csrf().disable() // We don't need CSRF for JWT based authentication
				.exceptionHandling()
				.authenticationEntryPoint(new RestAuthenticationEntryPoint())
				.accessDeniedHandler(new MyAccessDeniedHandler())
				.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.addFilterBefore(new CustomCorsFilter(), UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(buildAjaxLoginProcessingFilter(AUTHENTICATION_URL), UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(buildJwtTokenAuthenticationProcessingFilter(permitAllEndpointList,
						annotationPermitAllEndpointList, API_ROOT_URL), UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	@Override
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	@Bean
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
	@ConditionalOnBean(RedisConnectionFactory.class)
	RefreshTokenManager redisRefreshTokenManager(RedisConnectionFactory redisConnectionFactory) {
		return new RedisRefreshTokenManager(redisConnectionFactory);
	}

	@Bean
	@ConditionalOnBean(RedisConnectionFactory.class)
	AuthTokenManager redisAuthTokenManager(RedisConnectionFactory redisConnectionFactory) {
		return new RedisAuthTokenManager(redisConnectionFactory);
	}

}
