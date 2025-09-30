// src/main/java/com/yourcompany/smallbusinessinvoices/config/CorsConfig.java
package com.sazimtandabuzo.smallbusinessinvoices.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://production-alb-1780857463.eu-north-1.elb.amazonaws.com", "http://localhost:4200","http://small-business-alb-221567162.eu-north-1.elb.amazonaws.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowCredentials(true)
                .maxAge(3600);
    }
}