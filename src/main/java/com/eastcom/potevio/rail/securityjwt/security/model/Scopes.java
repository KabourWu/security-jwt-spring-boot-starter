package com.eastcom.potevio.rail.securityjwt.security.model;

/**
 * @author kabour
 * @date 2019/6/19
 */
public enum Scopes {
	REFRESH_TOKEN;

	public String authority() {
		return this.name();
	}
}
