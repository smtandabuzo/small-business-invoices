// src/main/java/com/yourcompany/smallbusinessinvoices/security/RateLimitFilter.java
package com.sazimtandabuzo.smallbusinessinvoices.security;

import com.sazimtandabuzo.smallbusinessinvoices.security.RateLimitService;
import com.sazimtandabuzo.smallbusinessinvoices.util.SecurityUtils;
import com.sazimtandabuzo.smallbusinessinvoices.exception.RateLimitExceededException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {
    @Autowired
    private RateLimitService rateLimitService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String clientIp = SecurityUtils.getClientIpAddress(request);
        String path = request.getRequestURI();

        // Apply rate limiting to API endpoints only
        if (path.startsWith("/api/")) {
            try {
                rateLimitService.checkRateLimit(clientIp + ":" + path);
            } catch (RateLimitExceededException e) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("{\"error\":\"Too many requests\", \"message\":\"" + e.getMessage() + "\"}");
                response.setContentType("application/json");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}