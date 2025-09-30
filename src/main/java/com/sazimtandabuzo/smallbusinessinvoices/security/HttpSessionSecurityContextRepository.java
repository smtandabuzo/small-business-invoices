// src/main/java/com/yourcompany/smallbusinessinvoices/security/HttpSessionSecurityContextRepository.java
package com.sazimtandabuzo.smallbusinessinvoices.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

@Component
public class HttpSessionSecurityContextRepository implements SecurityContextRepository {

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        return SecurityContextHolder.createEmptyContext();
    }

    @Override
    public void saveContext(SecurityContext context,
                            HttpServletRequest request,
                            HttpServletResponse response) {
        // No-op - stateless
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return false;
    }
}