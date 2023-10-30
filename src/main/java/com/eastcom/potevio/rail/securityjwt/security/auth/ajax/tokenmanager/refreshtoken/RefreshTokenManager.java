package com.eastcom.potevio.rail.securityjwt.security.auth.ajax.tokenmanager.refreshtoken;

import com.eastcom.potevio.rail.securityjwt.security.model.UserContext;

/**
 * refresh token 管理，定义的目的是 应对 提前 结束 refresh token 的使用
 *
 * @author kabour
 * @date 2019/6/9 20:23
 */
public interface RefreshTokenManager {

	/**
	 * 检查 refresh token 是否能 正常使用
	 *
	 * @param subject
	 * @return
	 */
	boolean verify(String subject, UserContext userContext);

	/**
	 * 登录用户信息持久化
	 *
	 * @param subject
	 */
	void persistent(String subject, UserContext userContext);

	/**
	 * 删除用户的所有 refresh token
	 *
	 * @param subject
	 * @return
	 */
	long deleteRefreshTokenBySubject(String subject);
}
