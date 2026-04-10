package com.platform.dashboard.config;

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
 * Swagger / OpenAPI 3 konfigürasyonu — Dashboard & Raporlama.
 *
 *   Swagger UI  → http://localhost:8083/swagger-ui.html
 *   OpenAPI JSON → http://localhost:8083/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Value("${server.port:8083}")
    private String serverPort;

    @Bean
    public OpenAPI dashboardOpenAPI() {
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
                .title("Platform Dashboard API")
                .version("1.0.0")
                .description("""
                        Multi-tenant White-Label Platform — Dashboard ve Raporlama Servisi.

                        Bu servis üzerinden:
                        - Dashboard oluşturabilir ve widget ekleyebilirsiniz.
                        - Widget veri kaynakları: SQL, REST_API, CBS, STATIC.
                        - Rapor tanımları oluşturabilir ve yönetebilirsiniz.
                        - Rapor türleri: TABLE, CHART, MAP, MIXED.
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
