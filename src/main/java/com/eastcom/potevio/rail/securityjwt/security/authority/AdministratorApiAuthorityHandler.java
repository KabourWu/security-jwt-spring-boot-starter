package com.eastcom.potevio.rail.securityjwt.security.authority;

/**
 * 管理员资源接口处理
 *
 * @author kabour
 * @date 2019/7/12 21:36
 */
public interface AdministratorApiAuthorityHandler {

	/**
	 * 注解 @ApiAuthority  Type.ADMINISTRATOR 类型的回调处理
	 * @param authorityName
	 * @param description
	 */
	void handle(String authorityName, String description);
}
