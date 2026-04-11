package com.platform.hr.config;

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

@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Value("${server.port:8085}")
    private String serverPort;

    @Bean
    public OpenAPI hrOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Platform HR API")
                        .version("1.0.0")
                        .description("""
                                Multi-tenant White-Label Platform — İnsan Kaynakları Modülü.

                                Bu servis üzerinden:
                                - Çalışan profilleri oluşturabilir ve yönetebilirsiniz.
                                - Departman ve pozisyon tanımları yapabilirsiniz.
                                - Çalışan durumu, izin ve performans bilgilerini takip edebilirsiniz.
                                """)
                        .contact(new Contact().name("Platform Team").email("info@platform.com")))
                .servers(List.of(new Server()
                        .url("http://localhost:" + serverPort)
                        .description("Geliştirme Sunucusu")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token girin (Bearer öneki olmadan).")));
    }
}
