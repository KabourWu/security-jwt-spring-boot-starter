package com.eastcom.potevio.rail.securityjwt.security.oauth2;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 获取AccessToken返回实体
 *
 * @author kabour
 * @since 2023/10/27 14:31
 */
public final class OAuth2AccessTokenResponse {

    /**
     * 样例：{"access_token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsic29zLXJlc291cmNlIl0sImdyYW50X3R5cGUiOiJhdXRob3JpemF0aW9uX2NvZGUiLCJ1c2VyX25hbWUiOiJ1bml0eSIsInNjb3BlIjpbInJlYWQiXSwiZXhwIjoxNjk4Njc3MzExLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiLCJST0xFX1VOSVRZIl0sImp0aSI6IjU0MTIyYzIzLTQ1MTQtNGJkYy1iZTA2LTQzMDRkMjJmZjFhOSIsImNsaWVudF9pZCI6InVuaXR5LWNsaWVudCJ9.lU5DsuWr2vTf7Von8KxKRnDMO_Uh2WNMKscPuQJzhrM","token_type":"bearer","refresh_token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsic29zLXJlc291cmNlIl0sImdyYW50X3R5cGUiOiJhdXRob3JpemF0aW9uX2NvZGUiLCJ1c2VyX25hbWUiOiJ1bml0eSIsInNjb3BlIjpbInJlYWQiXSwiYXRpIjoiNTQxMjJjMjMtNDUxNC00YmRjLWJlMDYtNDMwNGQyMmZmMWE5IiwiZXhwIjoxNzAxMjI2MTExLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiLCJST0xFX1VOSVRZIl0sImp0aSI6ImFmMmIzN2FhLWYwZjktNDZhZS05NGExLTViNWJkYjA0ZmQyNyIsImNsaWVudF9pZCI6InVuaXR5LWNsaWVudCJ9.9nTvTGLAptuQU0MRW1QGNLugnN8FkU_pZy4GxPWgNLQ","expires_in":43199,"scope":"read","jti":"54122c23-4514-4bdc-be06-4304d22ff1a9"}
     */

    @JSONField(name = "access_token")
    private String accessToken;

    @JSONField(name = "refresh_token")
    private String refreshToken;

    @JSONField(name = "token_type")
    private String tokenType;

    @JSONField(name = "expires_in")
    private Integer expiresIn;

    @JSONField(name = "scope")
    private String scope;

    @JSONField(name = "jti")
    private String jti;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }
}
