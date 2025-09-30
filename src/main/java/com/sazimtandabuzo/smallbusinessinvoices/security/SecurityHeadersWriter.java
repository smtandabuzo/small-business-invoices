// src/main/java/com/yourcompany/smallbusinessinvoices/security/SecurityHeadersWriter.java
package com.sazimtandabuzo.smallbusinessinvoices.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SecurityHeadersWriter implements HeaderWriter {

    private final Map<String, String> headers = new HashMap<>();

    public SecurityHeadersWriter() {
        headers.put("X-Content-Type-Options", "nosniff");
        headers.put("X-Frame-Options", "DENY");
        headers.put("X-XSS-Protection", "1; mode=block");
        headers.put("Referrer-Policy", "strict-origin-when-cross-origin");
        headers.put("Feature-Policy", "geolocation 'none'; midi 'none'; sync-xhr 'none'; microphone 'none'; camera 'none'; magnetometer 'none'; gyroscope 'none'; fullscreen 'self'; payment 'none'");
        headers.put("Content-Security-Policy", "default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self' data:");
        headers.put("Strict-Transport-Security", "max-age=31536000 ; includeSubDomains");
        headers.put("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
    }

    @Override
    public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {
        headers.forEach(response::setHeader);
    }
}