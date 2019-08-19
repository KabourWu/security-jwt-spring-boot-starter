package online.kabour.springbootbase.securityjwt.security.auth.ajax.tokenmanager.refreshtoken;

import online.kabour.springbootbase.securityjwt.security.config.JwtSettings;
import online.kabour.springbootbase.securityjwt.security.model.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

/**
 * redis persistent refresh token and maintain
 *
 * @author kabour
 * @date 2019/6/20 21:08
 */
public class RedisRefreshTokenManager implements RefreshTokenManager {

	private static final String REFRESH_TOKEN_PACKAGE_NAME = "jwt:refresh-token";

	private final RedisTemplate<String, UserContext> redisTemplate;

	@Autowired
	private JwtSettings jwtSettings;

	@Autowired
	public RedisRefreshTokenManager(RedisConnectionFactory redisConnectionFactory) {
		this.redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(UserContext.class));
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.afterPropertiesSet();
	}

	@Override
	public boolean verify(String subject, UserContext userContext) {
		String redisKeyPattern = String.format("%s:%s:%s:%s", REFRESH_TOKEN_PACKAGE_NAME, "*", "*", subject);
		return redisTemplate.keys(redisKeyPattern).isEmpty() ? false : true;
	}

	@Override
	public void persistent(String subject, UserContext userContext) {
		Assert.notNull(userContext, "UserContext must not be null");
		String redisKey = String.format("%s:%s:%s:%s", REFRESH_TOKEN_PACKAGE_NAME, userContext.getUserType(), userContext.getAgentType(), subject);
		redisTemplate.opsForValue().set(redisKey, userContext, jwtSettings.getRefreshTokenExpTime(), TimeUnit.MINUTES);
	}

	@Override
	public long deleteRefreshTokenBySubject(String subject) {
		String redisKeyPattern = String.format("%s:%s:%s:%s", REFRESH_TOKEN_PACKAGE_NAME, "*", "*", subject);
		return redisTemplate.delete(redisTemplate.keys(redisKeyPattern));
	}
}
