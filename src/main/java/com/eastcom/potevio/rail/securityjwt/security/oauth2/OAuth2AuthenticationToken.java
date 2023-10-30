package com.eastcom.potevio.rail.securityjwt.security.oauth2;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

/**
 * 单点登录认证token
 *
 * @author kabour
 * @since 2023/10/27 13:54
 */
public class OAuth2AuthenticationToken extends AbstractAuthenticationToken {

    /**
     * 授权码
     */
    private String code;

    /**
     * 状态码
     */
    private String state;

    /**
     * 当事人对象
     */
    private Object principal;

    public OAuth2AuthenticationToken(String code, String state) {
        super(Collections.emptySet());
        this.code = code;
        this.state = state;
    }

    public OAuth2AuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    public void setPrincipal(Object obj) {
        this.principal = obj;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
