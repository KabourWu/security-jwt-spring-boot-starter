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
import online.kabour.springbootbase.securityjwt.security.auth.jwt.JwtTokenAuthenticationProcessingFilter;
import online.kabour.springbootbase.securityjwt.security.oauth2.MyOAuth2LoginAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
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
	@Autowired
	private OAuth2AuthorizationCodeAuthenticationProvider oAuth2AuthorizationCodeAuthenticationProvider;
	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository;
	@Autowired
	private InMemoryClientRegistrationRepository inMemoryClientRegistrationRepository;

	protected AjaxLoginProcessingFilter buildAjaxLoginProcessingFilter(String loginEntryPoint) throws Exception {
		AjaxLoginProcessingFilter filter = new AjaxLoginProcessingFilter(loginEntryPoint, successHandler, failureHandler);
		filter.setAuthenticationManager(authenticationManagerBean());
		return filter;
	}

	protected OAuth2LoginAuthenticationFilter buildOAuth2LoginAuthenticationFilter() {
		InMemoryOAuth2AuthorizedClientService inMemoryOAuth2AuthorizedClientService = new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
		return new MyOAuth2LoginAuthenticationFilter(inMemoryClientRegistrationRepository, inMemoryOAuth2AuthorizedClientService, successHandler, failureHandler);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(ajaxAuthenticationProvider);
		auth.authenticationProvider(jwtAuthenticationProvider);
		auth.authenticationProvider(oAuth2AuthorizationCodeAuthenticationProvider);
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
				.addFilterBefore(buildOAuth2LoginAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
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
