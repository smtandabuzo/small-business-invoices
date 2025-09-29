package com.sazimtandabuzo.smallbusinessinvoices.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${server.port:8081}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:" + serverPort);
        server.setDescription("Development server");

        Contact contact = new Contact();
        contact.setName("Support");
        contact.setEmail("support@smallbusiness.com");

        return new OpenAPI()
                .servers(List.of(server))
                .info(new Info()
                        .title("Small Business Invoices API")
                        .version("1.0.0")
                        .description("API for managing small business invoices and payments")
                        .contact(contact)
                        .license(new License()
                                .name("MIT License")
                                .url("https://choosealicense.com/licenses/mit/")
                        )
                );
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public-apis")
                .pathsToMatch("/api/**")
                .build();
    }
}
