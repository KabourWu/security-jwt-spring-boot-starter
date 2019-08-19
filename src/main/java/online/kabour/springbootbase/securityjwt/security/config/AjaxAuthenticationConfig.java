package online.kabour.springbootbase.securityjwt.security.config;


import online.kabour.springbootbase.securityjwt.security.CaptchaVerify;
import online.kabour.springbootbase.securityjwt.security.auth.ajax.service.MyAdminDetailsService;
import online.kabour.springbootbase.securityjwt.security.auth.ajax.service.MyMemberDetailsService;

/**
 * @author kabour
 * @date 2019/6/9 13:47
 */
public interface AjaxAuthenticationConfig {
	CaptchaVerify getCaptchaVerify();

	Byte getSerialTrialCount();

	Byte getLockedDuration();

	MyMemberDetailsService getMemberDetailsService();

	MyAdminDetailsService getAdminDetailsService();

}
