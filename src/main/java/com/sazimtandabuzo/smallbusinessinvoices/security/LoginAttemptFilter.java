// src/main/java/com/yourcompany/smallbusinessinvoices/security/LoginAttemptFilter.java
package com.sazimtandabuzo.smallbusinessinvoices.security;

import com.sazimtandabuzo.smallbusinessinvoices.security.LoginAttemptService;
import com.sazimtandabuzo.smallbusinessinvoices.util.SecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class LoginAttemptFilter extends OncePerRequestFilter {
    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String ip = getClientIP(request);
        if (loginAttemptService.isBlocked(ip)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Too many login attempts. Please try again later.");
            return;
        }
        filterChain.doFilter(request, response);
    }

    // Update the getClientIP method in LoginAttemptFilter
    private String getClientIP(HttpServletRequest request) {
        return SecurityUtils.getClientIpAddress(request);
    }
}