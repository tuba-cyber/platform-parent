package com.platform.cbs.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger / OpenAPI 3 konfigürasyonu — CBS (Coğrafi Bilgi Sistemi).
 *
 *   Swagger UI  → http://localhost:8082/swagger-ui.html
 *   OpenAPI JSON → http://localhost:8082/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Value("${server.port:8082}")
    private String serverPort;

    @Bean
    public OpenAPI cbsOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Geliştirme Sunucusu")
                ))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, jwtSecurityScheme())
                );
    }

    private Info apiInfo() {
        return new Info()
                .title("Platform CBS API")
                .version("1.0.0")
                .description("""
                        Multi-tenant White-Label Platform — Coğrafi Bilgi Sistemi (CBS) Servisi.

                        Bu servis üzerinden:
                        - Katman (Layer) tanımları oluşturabilirsiniz (VECTOR, RASTER, WMS, WFS).
                        - GeoJSON formatında geometrik özellikler (Feature) ekleyebilirsiniz.
                        - PostGIS tabanlı coğrafi sorgular yapabilirsiniz.
                        """)
                .contact(new Contact()
                        .name("Platform Team")
                        .email("info@platform.com"));
    }

    private SecurityScheme jwtSecurityScheme() {
        return new SecurityScheme()
                .name(BEARER_AUTH)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT token girin (Bearer öneki olmadan).");
    }
}
