package com.eastcom.potevio.rail.securityjwt.security.auth.jwt;

import com.eastcom.potevio.rail.securityjwt.security.JwtTokenHelper;
import com.eastcom.potevio.rail.securityjwt.security.auth.JwtAuthenticationToken;
import com.eastcom.potevio.rail.securityjwt.security.config.JwtWebSecurityConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author kabour
 * @date 2019/6/19
 */
public class JwtTokenAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

	private final JwtTokenHelper jwtTokenUtils;

	private final AuthenticationFailureHandler failureHandler;

	public JwtTokenAuthenticationProcessingFilter(JwtTokenHelper jwtTokenHelper,
	                                              AuthenticationFailureHandler failureHandler,
	                                              RequestMatcher requiresAuthenticationRequestMatcher) {
		super(requiresAuthenticationRequestMatcher);
		logger.info("初始化Jwt Authentication Filter");
		this.failureHandler = failureHandler;
		this.jwtTokenUtils = jwtTokenHelper;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		String tokenPayload = request.getHeader(JwtWebSecurityConfig.AUTHENTICATION_HEADER_NAME);
		String rawAccessToken = jwtTokenUtils.extract(tokenPayload);
		return getAuthenticationManager().authenticate(new JwtAuthenticationToken(rawAccessToken));
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
	                                        Authentication authResult) throws IOException, ServletException {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authResult);
		SecurityContextHolder.setContext(context);
		chain.doFilter(request, response);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
	                                          AuthenticationException failed) throws IOException, ServletException {
		SecurityContextHolder.clearContext();
		failureHandler.onAuthenticationFailure(request, response, failed);
	}
}
