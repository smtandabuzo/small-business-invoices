package com.sazimtandabuzo.smallbusinessinvoices.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${server.port:8081}")
    private String serverPort;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Value("${app.api.docs.enabled:false}")
    private boolean apiDocsEnabled;

    @Value("${app.api.url:http://localhost:8081}")
    private String apiUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        // Common API info
        Contact contact = new Contact()
                .name("Small Business Invoices Support")
                .email("support@smallbusiness.com")
                .url("https://smallbusiness.com/support");

        License license = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        // Server configurations
        Server devServer = createServer(
                "http://localhost:" + serverPort,
                "Development Server",
                "Local development environment"
        );

        Server prodServer = createServer(
                apiUrl,
                "Production Server",
                "Production environment"
        );

        // Security scheme for JWT
        SecurityScheme securityScheme = new SecurityScheme()
                .name("JWT")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT Authorization header using the Bearer scheme. Example: \"Authorization: Bearer {token}\"");

        return new OpenAPI()
                .servers(List.of(isProduction() ? prodServer : devServer))
                .components(new Components()
                        .addSecuritySchemes("JWT", securityScheme)
                )
                .security(Collections.singletonList(
                        new SecurityRequirement().addList("JWT")
                ))
                .info(new Info()
                        .title("Small Business Invoices API")
                        .version("1.0.0")
                        .description("""
                                ### REST API for Small Business Invoices Management
                                
                                This API provides endpoints for managing:
                                - Invoice creation, retrieval, and management
                                - Customer information handling
                                - Payment processing
                                - Reporting and analytics
                                
                                **Note:** This API requires authentication via JWT token.
                                """)
                        .contact(contact)
                        .license(license)
                );
    }

    @Bean
    @Profile("!prod")
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch(
                        "/api/auth/**",
                        "/api/public/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                )
                .build();
    }

    @Bean
    @Profile("prod")
    public GroupedOpenApi protectedApi() {
        return GroupedOpenApi.builder()
                .group("protected")
                .pathsToMatch(
                        "/api/invoices/**",
                        "/api/customers/**",
                        "/api/payments/**"
                )
                .addOpenApiCustomizer(openApi -> 
                    openApi.info(new Info()
                        .title("Protected API Endpoints")
                        .description("These endpoints require authentication")
                        .version("1.0.0"))
                )
                .build();
    }

    private Server createServer(String url, String name, String description) {
        Server server = new Server();
        server.setUrl(url);
        // Note: Server class doesn't have a setName() method in this version of OpenAPI
        server.setDescription(description);
        return server;
    }

    private boolean isProduction() {
        return activeProfile != null && (activeProfile.contains("prod") || activeProfile.contains("production"));
    }
    }
