package com.eastcom.potevio.rail.securityjwt.security.exceptions;

import org.springframework.security.authentication.AuthenticationServiceException;

/**
 * @author kabour
 * @date 2019/6/19
 */
public class AuthMethodNotSupportedException extends AuthenticationServiceException {
    private static final long serialVersionUID = 3705043083010304496L;

    public AuthMethodNotSupportedException(String msg) {
        super(msg);
    }
}
