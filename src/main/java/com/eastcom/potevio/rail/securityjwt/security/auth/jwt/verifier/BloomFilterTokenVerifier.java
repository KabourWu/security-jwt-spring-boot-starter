package com.eastcom.potevio.rail.securityjwt.security.auth.jwt.verifier;

import org.springframework.stereotype.Component;

/**
 * @author kabour
 * @date 2019/6/19
 */
@Component
public class BloomFilterTokenVerifier implements TokenVerifier {
    @Override
    public boolean verify(String jti) {
        return true;
    }
}
