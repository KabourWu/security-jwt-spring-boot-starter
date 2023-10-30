package com.eastcom.potevio.rail.securityjwt.security;

import com.eastcom.potevio.rail.securityjwt.security.auth.JwtAuthenticationToken;
import com.eastcom.potevio.rail.securityjwt.security.auth.ajax.LoginRequest;
import com.eastcom.potevio.rail.securityjwt.security.model.UserContext;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

/**
 * 获取当前登录用户工具类
 *
 * @author kabour
 * @date 2019/6/19 19:16
 */
public class PrincipalHelper {

	/**
	 * 获取登录会员的基本信息
	 *
	 * @return
	 */
	public static UserContext.UserBasicDetail userBasicDetail() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return null;
		}
		//使用Mock单元测试情况
		if (authentication instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) authentication;
			String name = usernamePasswordAuthenticationToken.getName();
			return new UserContext.UserBasicDetail(name, name, "");
		}
		JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
		UserContext userContext = (UserContext) jwtAuthenticationToken.getPrincipal();
		return userContext.getUserBasicDetail();
	}

	/**
	 * 检查是否是当事人
	 *
	 * @param principalIdentity
	 * @return
	 */
	public static boolean principalCheck(String principalIdentity) {
		return Objects.equals(principalIdentity, principalIdentity());
	}

	/**
	 * 获取登录用户的 唯一标识
	 *
	 * @return
	 */
	public static String principalIdentity() {
		return userBasicDetail() == null ? null : userBasicDetail().getIdentity();
	}

	/**
	 * 获取登录用户的用户名
	 *
	 * @return
	 */
	public static String principalUsername() {
		return userBasicDetail() == null ? null : userBasicDetail().getUsername();
	}

	/**
	 * 获取登录用户的头像
	 *
	 * @return
	 */
	public static String principalIcon() {
		return userBasicDetail() == null ? null : userBasicDetail().getIcon();
	}

	/**
	 * 获取登录用户的类型
	 *
	 * @return
	 */
	public static LoginRequest.UserType principalType() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof JwtAuthenticationToken) {
			UserContext userContext = (UserContext) authentication.getPrincipal();
			return userContext.getUserType();
		}
		return LoginRequest.UserType.ANONYMOUS;
	}

	/**
	 * 获取登录认证时 自定义的注册信息
	 *
	 * @param key
	 * @return
	 */
	public static Object extInfo(String key) {
		return userBasicDetail() == null ? null : userBasicDetail().getExtInfo(key);
	}
}
