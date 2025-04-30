package com.github.phfbueno.hubspotintegration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "hubspot")
public class HubspotConfig {

    private String authUrl;
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String scopes;
    private String tokenUrl;
    private String contactUrl;

    public String getAuthUrl() {
        return authUrl;
    }

    public String getClientId() {
        return clientId;
    }


    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }


    public String getScopes() {
        return scopes;
    }


    public String getTokenUrl() {
        return tokenUrl;
    }

    public String getContactUrl() {
        return contactUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    public void setContactUrl(String contactUrl) {
        this.contactUrl = contactUrl;
    }
}
