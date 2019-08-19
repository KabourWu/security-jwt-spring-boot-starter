package online.kabour.springbootbase.securityjwt.security.auth.jwt;

import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import online.kabour.springbootbase.securityjwt.security.JwtTokenHelper;
import online.kabour.springbootbase.securityjwt.security.auth.JwtAuthenticationToken;
import online.kabour.springbootbase.securityjwt.security.model.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kabour
 * @date 2019/6/19
 */
@SuppressWarnings("unchecked")
public class JwtAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private JwtTokenHelper jwtTokenHelper;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String rawAccessToken = (String) authentication.getCredentials();

		Jws<Claims> jwsClaims = jwtTokenHelper.parseAccessToken(rawAccessToken);
		String subject = jwsClaims.getBody().getSubject();
		List<String> scopes = jwsClaims.getBody().get("scopes", List.class);
		List<GrantedAuthority> authorities = scopes.stream()
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
		String jsonString = JSONObject.toJSONString(jwsClaims.getBody().get("detail", LinkedHashMap.class));
		UserContext.UserBasicDetail userBasicDetail = JSONObject.parseObject(jsonString, UserContext.UserBasicDetail.class);
		UserContext context = new UserContext(subject, jwtTokenHelper.userType(jwsClaims),
				jwtTokenHelper.agentType(jwsClaims), userBasicDetail, authorities);
		return new JwtAuthenticationToken(context, context.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
	}
}
