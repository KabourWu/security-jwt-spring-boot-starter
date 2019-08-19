package online.kabour.springbootbase.securityjwt.security.auth.ajax;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author kabour
 * @date 2019/6/19
 */
public class LoginRequest {

	private String username;
	@JsonIgnore
	private String password;
	private String captcha;
	private UserType type; //用户类型
	private AgentType agentType; //用户代理类型,app 还是 web 等

	@JsonCreator
	public LoginRequest(@JsonProperty("username") String username, @JsonProperty("password") String password,
	                    @JsonProperty("captcha") String captcha, @JsonProperty("userType") UserType type, @JsonProperty("agentType") AgentType agentType) {
		this.username = username;
		this.password = password;
		this.captcha = captcha;
		this.type = type;
		this.agentType = agentType;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getCaptcha() {
		return captcha;
	}

	public UserType getType() {
		return type;
	}

	public AgentType getAgentType() {
		return agentType;
	}

	/**
	 * 清除密码
	 */
	public void eraserPassword() {
		password = null;
	}

	/**
	 * 用户代理类型
	 */
	public enum AgentType {
		APP, WEB
	}

	/**
	 * 用户类型
	 */
	public enum UserType {
		ADMIN, MEMBER, ANONYMOUS
	}
}
