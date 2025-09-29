package com.sazimtandabuzo.smallbusinessinvoices.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.security.jwt")
public class JwtConfig {
    private String secret;
    private long expirationMs;
    private long refreshExpirationMs;

    // Getters and Setters
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public void setExpirationMs(String expirationMs) {
        // Handle the case where the value might contain a comment
        if (expirationMs != null) {
            this.expirationMs = Long.parseLong(expirationMs.split("#")[0].trim());
        } else {
            this.expirationMs = 86400000L; // 24 hours default
        }
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }

    public void setRefreshExpirationMs(String refreshExpirationMs) {
        // Handle the case where the value might contain a comment
        if (refreshExpirationMs != null) {
            this.refreshExpirationMs = Long.parseLong(refreshExpirationMs.split("#")[0].trim());
        } else {
            this.refreshExpirationMs = 604800000L; // 7 days default
        }
    }
}
