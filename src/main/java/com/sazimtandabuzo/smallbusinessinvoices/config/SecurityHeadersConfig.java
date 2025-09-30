// src/main/java/com/yourcompany/smallbusinessinvoices/config/SecurityHeadersConfig.java
package com.sazimtandabuzo.smallbusinessinvoices.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityHeadersConfig implements WebMvcConfigurer {

    @Bean
    public XXssProtectionHeaderWriter xXssProtectionHeaderWriter() {
        return new XXssProtectionHeaderWriter();
    }
}
