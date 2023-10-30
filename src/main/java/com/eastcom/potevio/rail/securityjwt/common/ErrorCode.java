package com.eastcom.potevio.rail.securityjwt.common;

/**
 * @author kabour
 * @date 2019/6/19
 */
public enum ErrorCode {
	BAD_CREDENTIALS(401),
	AUTH_METHOD_NOT_SUPPORTED(405),
	AUTHENTICATION_FAILED(401),
	INVALID_JWT_TOKEN(1401),
	INVALID_REFRESH_TOKEN(2401),
	ACCESS_DENIED(403);
	private int errorCode;

	ErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}
}
