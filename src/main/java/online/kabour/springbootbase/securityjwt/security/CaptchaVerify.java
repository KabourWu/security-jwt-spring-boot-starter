package online.kabour.springbootbase.securityjwt.security;

/**
 * 校验码认证
 * @author kabour
 * @date 2019/6/9 11:20
 */
public interface CaptchaVerify {

	/**
	 * 核实校验码
	 * @param username
	 * @param captcha
	 * @return
	 */
	boolean verify(String username, String captcha);
}
