package com.eastcom.potevio.rail.securityjwt.security.auth.jwt.verifier;

/**
 * @author kabour
 * @date 2019/6/19
 */
public interface TokenVerifier {
    boolean verify(String jti);
}
