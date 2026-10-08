package com.clinic.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) Configuration.
 *
 * This configures the auto-generated API documentation.
 * Access it at: http://localhost:8080/swagger-ui.html
 *
 * It also adds a "Authorize" button to the Swagger UI so you can
 * paste your JWT token and test authenticated endpoints directly.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configure the OpenAPI metadata and security scheme.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // API information shown at the top of Swagger UI
                .info(new Info()
                        .title("Clinic Management API")
                        .version("1.0.0")
                        .description("A complete Clinic Management REST API with authentication, " +
                                "authorization, pagination, and comprehensive CRUD operations " +
                                "for patients, doctors, and appointments.")
                        .contact(new Contact()
                                .name("Clinic App")
                                .email("admin@clinic.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                // Add JWT Bearer token security scheme to Swagger UI
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter your JWT token in the format: Bearer <token>")))
                // Require JWT authentication for all endpoints by default
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
