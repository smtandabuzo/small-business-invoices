// src/main/java/com/yourcompany/smallbusinessinvoices/security/SecurityEventLogger.java
package com.sazimtandabuzo.smallbusinessinvoices.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class SecurityEventLogger {
    private static final Logger log = LoggerFactory.getLogger(SecurityEventLogger.class);

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        log.info("Successful login: {}", event.getAuthentication().getName());
    }

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        log.warn("Failed login attempt for user: {}", event.getAuthentication().getName());
    }
}