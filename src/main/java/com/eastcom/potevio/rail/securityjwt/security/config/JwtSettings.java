package com.eastcom.potevio.rail.securityjwt.security.config;

import com.eastcom.potevio.rail.securityjwt.security.model.token.JwtToken;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author kabour
 * @date 2019/6/19
 */
@ConfigurationProperties(prefix = "security.jwt")
public class JwtSettings {
    /**
     * {@link JwtToken} will expire after this time.
     */
    private Integer tokenExpirationTime = 10;

    /**
     * Token issuer.
     */
    private String tokenIssuer;

    /**
     * Key is used to sign {@link JwtToken}.
     */
    private String tokenSigningKey;

    /**
     * {@link JwtToken} can be refreshed during this timeframe.
     * default 14400 minute (10 days)
     */
    private Integer refreshTokenExpTime = 14400;

    /**
     * refresh token is reuse.
     */
    private Boolean refreshTokenReuse = false;

    /**
     * @IgonoreAuthorize scan base packages,include sub packages,attention please!;
     */
    private String[] annotationPackages;

    /**
     * permit endpoints, skip authorize authority.
     */
    private String[] permitEndpoints;

    public Integer getRefreshTokenExpTime() {
        return refreshTokenExpTime;
    }

    public void setRefreshTokenExpTime(Integer refreshTokenExpTime) {
        this.refreshTokenExpTime = refreshTokenExpTime;
    }

    public Integer getTokenExpirationTime() {
        return tokenExpirationTime;
    }

    public void setTokenExpirationTime(Integer tokenExpirationTime) {
        this.tokenExpirationTime = tokenExpirationTime;
    }

    public String getTokenIssuer() {
        return tokenIssuer;
    }

    public void setTokenIssuer(String tokenIssuer) {
        this.tokenIssuer = tokenIssuer;
    }

    public String getTokenSigningKey() {
        return tokenSigningKey;
    }

    public void setTokenSigningKey(String tokenSigningKey) {
        this.tokenSigningKey = tokenSigningKey;
    }

    public Boolean getRefreshTokenReuse() {
        return refreshTokenReuse;
    }

    public void setRefreshTokenReuse(Boolean refreshTokenReuse) {
        this.refreshTokenReuse = refreshTokenReuse;
    }

    public String[] getAnnotationPackages() {
        return annotationPackages;
    }

    public void setAnnotationPackages(String[] annotationPackages) {
        this.annotationPackages = annotationPackages;
    }

    public String[] getPermitEndpoints() {
        return permitEndpoints;
    }

    public void setPermitEndpoints(String[] permitEndpoints) {
        this.permitEndpoints = permitEndpoints;
    }
}
