package online.kabour.springbootbase.securityjwt.security.auth.ajax.tokenmanager.authtoken;

import online.kabour.springbootbase.securityjwt.security.config.JwtSettings;
import online.kabour.springbootbase.securityjwt.security.model.UserContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
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
public class RedisAuthTokenManager implements AuthTokenManager, ApplicationContextAware {

	private static final String AUTH_TOKEN_PACKAGE_NAME = "jwt:auth-token";

	private final RedisTemplate<String, UserContext.UserBasicDetail> redisTemplate;

	@Autowired
	private JwtSettings jwtSettings;

	private RedisConnectionFactory redisConnectionFactory;

	@Autowired
	public RedisAuthTokenManager() {
		this.redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(UserContext.class));
	}

	@Override
	public void persistent(String subject, UserContext userContext) {
		Assert.notNull(userContext, "UserContext must not be null");
		String redisKey = String.format("%s:%s:%s:%s", AUTH_TOKEN_PACKAGE_NAME, userContext.getUserType(), userContext.getAgentType(), subject);
		redisTemplate.opsForValue().set(redisKey, userContext.getUserBasicDetail(), jwtSettings.getTokenExpirationTime(), TimeUnit.MINUTES);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.redisConnectionFactory = applicationContext.getBean(RedisConnectionFactory.class);
		redisTemplate.setConnectionFactory(this.redisConnectionFactory);
		redisTemplate.afterPropertiesSet();
	}
}
