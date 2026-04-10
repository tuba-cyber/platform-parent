package com.platform.co.config;

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

    @Value("${server.port:8084}")
    private String serverPort;

    @Bean
    public OpenAPI companyOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Platform Company API")
                        .version("1.0.0")
                        .description("""
                                Multi-tenant White-Label Platform — Şirket ve Şube Yönetimi.

                                Bu servis üzerinden:
                                - Platforma yeni şirket/kurum ekleyebilirsiniz.
                                - Şirket bilgilerini (vergi no, sektör, adres vb.) yönetebilirsiniz.
                                - Her şirkete ait şube ve lokasyonları tanımlayabilirsiniz.
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
