package com.eastcom.potevio.rail.securityjwt.security.oauth2;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * sso客户端配置参数
 * @author kabour
 * @since 2023/10/27 14:14
 */
@Component
@ConfigurationProperties(prefix = "security.oauth2.client")
public class OAuth2ClientProperties {

    public static final String KEY_AUTH2 = "auth2";

    /**
     * OAuth provider details.
     */
    private final Map<String, Provider> provider = new HashMap<>();

    /**
     * OAuth client registrations.
     */
    private final Map<String, Registration> registration = new HashMap<>();

    public Map<String, Provider> getProvider() {
        return this.provider;
    }

    public Map<String, Registration> getRegistration() {
        return this.registration;
    }

    @PostConstruct
    public void validate() {
        getRegistration().values().forEach(this::validateRegistration);
    }

    private void validateRegistration(Registration registration) {
        if (!StringUtils.hasText(registration.getClientId())) {
            throw new IllegalStateException("Client id must not be empty.");
        }
    }

    /**
     * A single client registration.
     */
    public static class Registration {

        /**
         * Reference to the OAuth 2.0 provider to use. May reference an element from the
         * 'provider' property or used one of the commonly used providers (google, github,
         * facebook, okta).
         */
        private String provider;

        /**
         * Client ID for the registration.
         */
        private String clientId;

        /**
         * Client secret of the registration.
         */
        private String clientSecret;

        /**
         * Client authentication method. May be left blank when using a pre-defined
         * provider.
         */
        private String clientAuthenticationMethod;

        /**
         * Authorization grant type. May be left blank when using a pre-defined provider.
         */
        private String authorizationGrantType;

        /**
         * Redirect URI. May be left blank when using a pre-defined provider.
         */
        private String redirectUri;

        /**
         * Authorization scopes. May be left blank when using a pre-defined provider.
         */
        private Set<String> scope;

        /**
         * Client name. May be left blank when using a pre-defined provider.
         */
        private String clientName;

        public String getProvider() {
            return this.provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getClientId() {
            return this.clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return this.clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getClientAuthenticationMethod() {
            return this.clientAuthenticationMethod;
        }

        public void setClientAuthenticationMethod(String clientAuthenticationMethod) {
            this.clientAuthenticationMethod = clientAuthenticationMethod;
        }

        public String getAuthorizationGrantType() {
            return this.authorizationGrantType;
        }

        public void setAuthorizationGrantType(String authorizationGrantType) {
            this.authorizationGrantType = authorizationGrantType;
        }

        public String getRedirectUri() {
            return this.redirectUri;
        }

        public void setRedirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
        }

        public Set<String> getScope() {
            return this.scope;
        }

        public void setScope(Set<String> scope) {
            this.scope = scope;
        }

        public String getClientName() {
            return this.clientName;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

    }

    public static class Provider {

        /**
         * 是否本地校验用户
         */
        private boolean userLocalCheck = false;

        /**
         * Authorization URI for the provider.
         */
        private String authorizationUri;

        /**
         * Token URI for the provider.
         */
        private String tokenUri;

        /**
         * User info URI for the provider.
         */
        private String userInfoUri;

        /**
         * User info authentication method for the provider.
         */
        private String userInfoAuthenticationMethod;

        /**
         * Name of the attribute that will be used to extract the username from the call
         * to 'userInfoUri'.
         */
        private String userNameAttribute;

        /**
         * JWK set URI for the provider.
         */
        private String jwkSetUri;

        /**
         * URI that can either be an OpenID Connect discovery endpoint or an OAuth 2.0
         * Authorization Server Metadata endpoint defined by RFC 8414.
         */
        private String issuerUri;

        public String getAuthorizationUri() {
            return this.authorizationUri;
        }

        public void setAuthorizationUri(String authorizationUri) {
            this.authorizationUri = authorizationUri;
        }

        public String getTokenUri() {
            return this.tokenUri;
        }

        public void setTokenUri(String tokenUri) {
            this.tokenUri = tokenUri;
        }

        public String getUserInfoUri() {
            return this.userInfoUri;
        }

        public void setUserInfoUri(String userInfoUri) {
            this.userInfoUri = userInfoUri;
        }

        public String getUserInfoAuthenticationMethod() {
            return this.userInfoAuthenticationMethod;
        }

        public void setUserInfoAuthenticationMethod(String userInfoAuthenticationMethod) {
            this.userInfoAuthenticationMethod = userInfoAuthenticationMethod;
        }

        public String getUserNameAttribute() {
            return this.userNameAttribute;
        }

        public void setUserNameAttribute(String userNameAttribute) {
            this.userNameAttribute = userNameAttribute;
        }

        public String getJwkSetUri() {
            return this.jwkSetUri;
        }

        public void setJwkSetUri(String jwkSetUri) {
            this.jwkSetUri = jwkSetUri;
        }

        public String getIssuerUri() {
            return this.issuerUri;
        }

        public void setIssuerUri(String issuerUri) {
            this.issuerUri = issuerUri;
        }

        public boolean isUserLocalCheck() {
            return userLocalCheck;
        }

        public void setUserLocalCheck(boolean userLocalCheck) {
            this.userLocalCheck = userLocalCheck;
        }
    }

}
