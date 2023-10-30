package com.eastcom.potevio.rail.securityjwt.security.auth.ajax.tokenmanager.authtoken;

import com.eastcom.potevio.rail.securityjwt.security.model.UserContext;

/**
 * token 扩展管理
 *
 * @author kabour
 * @date 2019/7/14 16:32
 */
public interface AuthTokenManager {

	/**
	 * 记录auth token信息
	 *
	 * @param subject
	 */
	void persistent(String subject, UserContext userContext);

}
