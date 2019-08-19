package online.kabour.springbootbase.securityjwt.security.config;

import online.kabour.springbootbase.securityjwt.component.AnnotationScanner;
import online.kabour.springbootbase.securityjwt.security.CustomCorsFilter;
import online.kabour.springbootbase.securityjwt.security.JwtTokenHelper;
import online.kabour.springbootbase.securityjwt.security.MyAccessDeniedHandler;
import online.kabour.springbootbase.securityjwt.security.RestAuthenticationEntryPoint;
import online.kabour.springbootbase.securityjwt.security.annotaion.IgnoreAuthorize;
import online.kabour.springbootbase.securityjwt.security.auth.ajax.tokenmanager.authtoken.AuthTokenManager;
import online.kabour.springbootbase.securityjwt.security.auth.ajax.tokenmanager.refreshtoken.NoOptRefreshTokenManager;
import online.kabour.springbootbase.securityjwt.security.auth.ajax.tokenmanager.refreshtoken.RefreshTokenManager;
import online.kabour.springbootbase.securityjwt.security.auth.jwt.JwtAuthenticationProvider;
import online.kabour.springbootbase.securityjwt.security.auth.jwt.JwtTokenAuthenticationProcessingFilter;
import online.kabour.springbootbase.securityjwt.security.auth.jwt.SkipPathRequestMatcher;
import online.kabour.springbootbase.securityjwt.security.authority.IgnoreAuthorizeAnnotationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author kabour
 * @date 2019/6/19
 */
@ConditionalOnProperty(name = "security.ajax.enabled", havingValue = "false", matchIfMissing = true)
public class JwtWebSecurityConfig extends WebSecurityConfigurerAdapter implements ApplicationContextAware {

	public static final Logger LOGGER = LoggerFactory.getLogger(JwtWebSecurityConfig.class);

	public static final String AUTHENTICATION_HEADER_NAME = "Authorization";
	public static final String AUTHENTICATION_URL = "/auth/token";
	public static final String REFRESH_TOKEN_URL = "/auth/refresh-token";
	public static final String API_ROOT_URL = "/**";
	protected List<String> permitAllEndpointList = new ArrayList<>(Arrays.asList(REFRESH_TOKEN_URL, AUTHENTICATION_URL));
	protected List<Object[]> annotationPermitAllEndpointList = new ArrayList<>();
	@Autowired
	private AuthenticationFailureHandler failureHandler;
	@Autowired
	private JwtAuthenticationProvider jwtAuthenticationProvider;
	@Autowired
	private JwtTokenHelper jwtTokenHelper;
	@Autowired
	private JwtSettings jwtSettings;

	private ApplicationContext applicationContext;


	protected JwtTokenAuthenticationProcessingFilter buildJwtTokenAuthenticationProcessingFilter(List<String> pathsToSkip, List<Object[]> mvcPathToSkip, String pattern) throws Exception {
		SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(applicationContext, pathsToSkip, mvcPathToSkip, pattern);
		JwtTokenAuthenticationProcessingFilter filter = new JwtTokenAuthenticationProcessingFilter(jwtTokenHelper, failureHandler, matcher);
		filter.setAuthenticationManager(authenticationManagerBean());
		return filter;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) {
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
				.addFilterBefore(buildJwtTokenAuthenticationProcessingFilter(permitAllEndpointList,
						annotationPermitAllEndpointList, API_ROOT_URL), UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	@Override
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	/**
	 * 方便后续配置添加 不用认证就能访问的 端点
	 *
	 * @param endpointList
	 * @return
	 */
	protected void addPermitEndpoint(List<String> endpointList) {
		String[] propertiesPermitEndpoints = jwtSettings.getPermitEndpoints();
		if (propertiesPermitEndpoints != null && propertiesPermitEndpoints.length > 0) {
			LOGGER.debug("AntMatcher style permit endpoints: {}", Arrays.toString(propertiesPermitEndpoints));
			endpointList.addAll(Arrays.asList(propertiesPermitEndpoints));
		}
	}

	/**
	 * 方便后续配置添加Mvc 风格忽略认证的端点
	 * 默认提供基于注解的方案
	 *
	 * @param mvcPermitEndpointList object[0] HttpMethod object[1] urls
	 * @return
	 */
	protected void addMvcPermitEndpoint(List<Object[]> mvcPermitEndpointList) {
		String[] annotationBasePackages = jwtSettings.getAnnotationPackages();
		if (annotationBasePackages == null || annotationBasePackages.length == 0) {
			LOGGER.info("未配置@IgnoreAuthorize 注解扫描基础包，此注解功能未启用");
			return;
		}
		LOGGER.info("配置了@IgnoreAuthorize 注解扫描基础包{}，此注解功能已启用", Arrays.toString(annotationBasePackages));
		IgnoreAuthorizeAnnotationHandler ignoreAuthorizeAnnotationHandler = new IgnoreAuthorizeAnnotationHandler();
		AnnotationScanner annotationScanner = new AnnotationScanner(IgnoreAuthorize.class, annotationBasePackages);
		annotationScanner.methodAnnotationHandle(ignoreAuthorizeAnnotationHandler).scan();
		List<Object[]> annotationMvcEndpoints = ignoreAuthorizeAnnotationHandler.getIgnoreEndpoints();
		mvcPermitEndpointList.addAll(annotationMvcEndpoints);
	}


	@Override
	public void setApplicationContext(ApplicationContext context) {
		super.setApplicationContext(context);
		this.applicationContext = context;
	}

	@Bean
	protected BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@ConditionalOnMissingBean
	AuthTokenManager authTokenManager() {
		//默认的 认证token 管理，不做任何操作
		return (subject, userContext) -> {
			//empty operate
		};
	}

	@Bean
	@ConditionalOnMissingBean
	RefreshTokenManager refreshTokenManager() {
		return new NoOptRefreshTokenManager();
	}
}
