package com.eastcom.potevio.rail.securityjwt.security.exceptions;


import org.springframework.security.core.AuthenticationException;

/**
 * @author kabour
 * @date 2019/6/19
 */
public class InvalidRefreshTokenException extends AuthenticationException {
	private static final long serialVersionUID = -294671188037098603L;

	public InvalidRefreshTokenException(String message) {
		super(message);
	}

	public InvalidRefreshTokenException() {
		super("Invalid refresh token");
	}
}
