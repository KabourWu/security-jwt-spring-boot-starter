package com.eastcom.potevio.rail.securityjwt.security.exceptions;

/**
 * @author kabour
 * @date 2019/6/19
 */
public class CaptchaInvalidException extends RuntimeException {

	private static final long serialVersionUID = -5104400435375328810L;

	public CaptchaInvalidException(String message) {
		super(message);
	}
}
