package com.eastcom.potevio.rail.securityjwt.security.config;


import com.eastcom.potevio.rail.securityjwt.security.CaptchaVerify;
import com.eastcom.potevio.rail.securityjwt.security.auth.ajax.service.MyAdminDetailsService;
import com.eastcom.potevio.rail.securityjwt.security.auth.ajax.service.MyMemberDetailsService;

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
