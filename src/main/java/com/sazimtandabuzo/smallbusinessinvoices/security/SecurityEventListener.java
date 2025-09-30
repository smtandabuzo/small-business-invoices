// src/main/java/com/yourcompany/smallbusinessinvoices/security/SecurityEventListener.java
package com.sazimtandabuzo.smallbusinessinvoices.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class SecurityEventListener {

    private static final Logger log = LoggerFactory.getLogger(SecurityEventListener.class);

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        log.info("Login successful for user: {}", username);
    }

    @EventListener
    public void onAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        String username = (String) event.getAuthentication().getPrincipal();
        log.warn("Login failed for user: {}, reason: {}", username, event.getException().getMessage());
    }

    @EventListener
    public void onLogoutSuccess(LogoutSuccessEvent event) {
        String username = event.getAuthentication().getName();
        log.info("Logout successful for user: {}", username);
    }
}