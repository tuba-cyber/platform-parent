package com.platform.gateway.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger / OpenAPI 3 konfigürasyonu.
 *
 * Uygulama ayağa kalktığında:
 *   Swagger UI  → http://localhost:8080/swagger-ui.html
 *   OpenAPI JSON → http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI gatewayOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Geliştirme Sunucusu")
                ))
                // Tüm endpoint'lere JWT güvenlik şeması uygula
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, jwtSecurityScheme())
                );
    }

    private Info apiInfo() {
        return new Info()
                .title("Platform Gateway API")
                .version("1.0.0")
                .description("""
                        Multi-tenant White-Label Platform — Kimlik Doğrulama ve Yetkilendirme Servisi.

                        **Nasıl kullanılır?**
                        1. `/api/v1/auth/login` endpoint'inden token alın.
                        2. Sağ üstteki **Authorize** butonuna tıklayın.
                        3. Token değerini girin (Bearer öneki olmadan).
                        4. Artık tüm korumalı endpoint'leri test edebilirsiniz.
                        """)
                .contact(new Contact()
                        .name("Platform Team")
                        .email("info@platform.com"))
                .license(new License()
                        .name("Proprietary")
                        .url("https://platform.com/license"));
    }

    /**
     * JWT Bearer Token güvenlik şeması.
     * Swagger UI'da sağ üstte "Authorize" butonu olarak görünür.
     */
    private SecurityScheme jwtSecurityScheme() {
        return new SecurityScheme()
                .name(BEARER_AUTH)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT token girin. Örnek: eyJhbGciOiJIUzI1NiJ9...");
    }
}
