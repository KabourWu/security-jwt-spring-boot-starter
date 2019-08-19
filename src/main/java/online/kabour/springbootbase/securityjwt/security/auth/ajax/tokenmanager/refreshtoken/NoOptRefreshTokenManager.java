package online.kabour.springbootbase.securityjwt.security.auth.ajax.tokenmanager.refreshtoken;

import online.kabour.springbootbase.securityjwt.security.model.UserContext;

/**
 * no operation refresh token manager
 *
 * @author kabour
 * @date 2019/6/20 21:05
 */
public class NoOptRefreshTokenManager implements RefreshTokenManager {
	@Override
	public boolean verify(String subject, UserContext userContext) {
		return true;
	}

	@Override
	public void persistent(String subject, UserContext userContext) {
		//empty operation
	}

	@Override
	public long deleteRefreshTokenBySubject(String subject) {
		throw new UnsupportedOperationException("此类型 token manager 不支持此操作！");
	}
}

