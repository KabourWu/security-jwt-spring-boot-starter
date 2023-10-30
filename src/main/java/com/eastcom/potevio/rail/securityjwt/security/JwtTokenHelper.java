package com.eastcom.potevio.rail.securityjwt.security;

import com.eastcom.potevio.rail.securityjwt.security.auth.ajax.LoginRequest;
import com.eastcom.potevio.rail.securityjwt.security.auth.ajax.tokenmanager.authtoken.AuthTokenManager;
import com.eastcom.potevio.rail.securityjwt.security.auth.ajax.tokenmanager.refreshtoken.RefreshTokenManager;
import com.eastcom.potevio.rail.securityjwt.security.config.JwtSettings;
import com.eastcom.potevio.rail.securityjwt.security.exceptions.InvalidJwtTokenException;
import com.eastcom.potevio.rail.securityjwt.security.exceptions.InvalidRefreshTokenException;
import com.eastcom.potevio.rail.securityjwt.security.model.Scopes;
import com.eastcom.potevio.rail.securityjwt.security.model.UserContext;
import com.eastcom.potevio.rail.securityjwt.security.model.token.AccessJwtToken;
import com.eastcom.potevio.rail.securityjwt.security.model.token.JwtToken;
import io.jsonwebtoken.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Factory class that should be always used to create {@link JwtToken}.
 */
public class JwtTokenHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenHelper.class);

	private static final String ACCESS_TOKEN_PREFIX = "Bearer ";
	@Autowired
	private JwtSettings settings;
	@Autowired
	private RefreshTokenManager refreshTokenManager;
	@Autowired
	private AuthTokenManager authTokenManager;

	/**
	 * 判断当前的用户类型
	 *
	 * @param jws
	 * @return
	 */
	public static LoginRequest.UserType userType(Jws<Claims> jws) {
		return LoginRequest.UserType.valueOf(jws.getBody().get("type", String.class));
	}

	/**
	 * 判断当前的用户类型
	 *
	 * @param jws
	 * @return
	 */
	public static LoginRequest.AgentType agentType(Jws<Claims> jws) {
		return LoginRequest.AgentType.valueOf(jws.getBody().get("agentType", String.class));
	}

	/**
	 * Factory method for issuing new JWT Tokens.
	 *
	 * @return
	 */
	public AccessJwtToken createAccessJwtToken(UserContext userContext) {
		Assert.notNull(settings.getTokenSigningKey(), " Sign key must not be null");
		if (StringUtils.isBlank(userContext.getUsername()))
			throw new IllegalArgumentException("Cannot create JWT Token without username");

		Claims claims = Jwts.claims().setSubject(userContext.getUsername());
		claims.put("scopes", userContext.getAuthorities().stream().map(s -> s.toString()).collect(Collectors.toList()));
		claims.put("type", userContext.getUserType());
		claims.put("agentType", userContext.getAgentType());
		claims.put("detail", userContext.getUserBasicDetail());

		LocalDateTime currentTime = LocalDateTime.now();

		String token = Jwts.builder()
				.setClaims(claims)
				.setIssuer(settings.getTokenIssuer())
				.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
				.setExpiration(Date.from(currentTime
						.plusMinutes(settings.getTokenExpirationTime())
						.atZone(ZoneId.systemDefault()).toInstant()))
				.signWith(SignatureAlgorithm.HS512, settings.getTokenSigningKey())
				.compact();

		authTokenManager.persistent(claims.getSubject(), userContext);
		return new AccessJwtToken(token, claims);
	}

	/**
	 * 根据 userContext 创建 refresh token
	 *
	 * @param userContext
	 * @return
	 */
	public JwtToken createRefreshToken(UserContext userContext) {
		Assert.notNull(settings.getTokenSigningKey(), " Sign key must not be null");
		if (StringUtils.isBlank(userContext.getUsername())) {
			throw new IllegalArgumentException("Cannot create JWT Token without username");
		}

		LocalDateTime currentTime = LocalDateTime.now();

		Claims claims = Jwts.claims().setSubject(userContext.getUsername());
		claims.put("scopes", Arrays.asList(Scopes.REFRESH_TOKEN.authority()));
		claims.put("type", userContext.getUserType());
		claims.put("agentType", userContext.getAgentType());

		String token = Jwts.builder()
				.setClaims(claims)
				.setIssuer(settings.getTokenIssuer())
				.setId(UUID.randomUUID().toString())
				.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
				.setExpiration(Date.from(currentTime
						.plusMinutes(settings.getRefreshTokenExpTime())
						.atZone(ZoneId.systemDefault()).toInstant()))
				.signWith(SignatureAlgorithm.HS512, settings.getTokenSigningKey())
				.compact();

		AccessJwtToken accessJwtToken = new AccessJwtToken(token, claims);
		refreshTokenManager.persistent(accessJwtToken.getClaims().getSubject(), userContext);
		return accessJwtToken;
	}

	/**
	 * 创建 access token  和 refresh token 编码对
	 *
	 * @param userContext
	 * @return
	 */
	public Map<String, String> createTokenPair(UserContext userContext) {
		Map<String, String> map = new LinkedHashMap<>(3);
		map.put("token", createAccessJwtToken(userContext).getToken());
		map.put("refreshToken", createRefreshToken(userContext).getToken());
		return map;
	}

	/**
	 * 解析 refresh token
	 *
	 * @param refreshToken
	 * @return
	 */
	public Jws<Claims> parseRefreshToken(String refreshToken) {
		Assert.notNull(settings.getTokenSigningKey(), " Sign key must not be null");
		Jws<Claims> jwsClaims;
		try {
			jwsClaims = parseAccessToken(refreshToken);
			Collection scopes = jwsClaims.getBody().get("scopes", Collection.class);
			if (scopes == null || scopes.isEmpty()
					|| !scopes.stream().anyMatch(scope -> Scopes.REFRESH_TOKEN.authority().equals(scope)
			)) throw new InsufficientAuthenticationException("Token scopes must contain refresh privilege");

		} catch (UnsupportedJwtException | MalformedJwtException
				| IllegalArgumentException | SignatureException | ExpiredJwtException ex) {
			LOGGER.error("Invalid Refresh JWT Token", ex.getMessage());
			throw new InvalidRefreshTokenException(ex.getMessage());
		}
		return jwsClaims;
	}

	/**
	 * 解析 access token
	 *
	 * @param accessToken
	 * @return
	 */
	public Jws<Claims> parseAccessToken(String accessToken) {
		try {
			return Jwts.parser().setSigningKey(settings.getTokenSigningKey()).parseClaimsJws(accessToken);
		} catch (UnsupportedJwtException | MalformedJwtException
				| IllegalArgumentException | SignatureException | ExpiredJwtException ex) {
			LOGGER.error("Invalid JWT Token", ex);
			throw new InvalidJwtTokenException(ex.getMessage());
		}
	}

	/**
	 * 提取 access token 密文
	 *
	 * @param header
	 * @return
	 */
	public String extract(String header) {
		if (StringUtils.isBlank(header)) {
			throw new AuthenticationServiceException("Authorization header cannot be blank!");
		}

		if (header.length() < ACCESS_TOKEN_PREFIX.length()) {
			throw new AuthenticationServiceException("Invalid authorization header size.");
		}

		if (!header.startsWith(ACCESS_TOKEN_PREFIX)) {
			throw new AuthenticationServiceException("Invalid authorization header, must starts with " + ACCESS_TOKEN_PREFIX);
		}

		return header.substring(ACCESS_TOKEN_PREFIX.length());
	}


}
