package com.eastcom.potevio.rail.securityjwt.security.model;

import com.eastcom.potevio.rail.securityjwt.security.auth.ajax.LoginRequest;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kabour
 * @date 2019/6/19
 */
public class UserContext implements Serializable {

	private final String username;
	private final LoginRequest.UserType userType; // 用户类型
	private final LoginRequest.AgentType agentType; //用户终端类型
	private final Collection<GrantedAuthority> authorities;
	private final UserBasicDetail userBasicDetail;

	@JsonCreator
	public UserContext(String username, LoginRequest.UserType userType, LoginRequest.AgentType agentType,
					   UserBasicDetail userBasicDetail, Collection<GrantedAuthority> authorities) {
		this.username = username;
		this.userType = userType;
		this.agentType = agentType;
		this.userBasicDetail = userBasicDetail;
		this.authorities = authorities;
	}

	public static UserContext create(String username, LoginRequest loginRequest, UserBasicDetail userBasicDetail, Collection<GrantedAuthority> authorities) {
		if (StringUtils.isBlank(username)) throw new IllegalArgumentException("Username is blank: " + username);
		if (loginRequest.getType() == null) throw new IllegalArgumentException("UserType must not be null");
		if (loginRequest.getAgentType() == null) throw new IllegalArgumentException("UserType must not be null");
		return new UserContext(username, loginRequest.getType(), loginRequest.getAgentType(), userBasicDetail, authorities);
	}

	public String getUsername() {
		return username;
	}

	public Collection<GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public UserBasicDetail getUserBasicDetail() {
		return userBasicDetail;
	}

	public LoginRequest.AgentType getAgentType() {
		return agentType;
	}

	public LoginRequest.UserType getUserType() {
		return userType;
	}

	public static class UserBasicDetail implements Serializable {

		private String identity;
		private String username;
		private String icon;
		private Map<String, Object> extInfoMap;

		public UserBasicDetail(String identity, String username, String icon) {
			this.identity = identity;
			this.username = username;
			this.icon = icon;
		}

		@JsonCreator
		public UserBasicDetail(String identity, String username, String icon, Map<String, Object> extInfoMap) {
			this.identity = identity;
			this.username = username;
			this.icon = icon;
			this.extInfoMap = extInfoMap;
		}

		public Map<String, Object> getExtInfoMap() {
			return extInfoMap;
		}

		public void setExtInfoMap(Map<String, Object> extInfoMap) {
			this.extInfoMap = extInfoMap;
		}

		public void registerExtInfo(String key, Object value) {
			if (extInfoMap == null) {
				extInfoMap = new HashMap<>();
			}
			extInfoMap.put(key, value);
		}

		public Object getExtInfo(String key) {
			return extInfoMap == null ? null : extInfoMap.get(key);
		}

		public String getIdentity() {
			return identity;
		}

		public String getIcon() {
			return icon;
		}

		public String getUsername() {
			return username;
		}

	}
}
