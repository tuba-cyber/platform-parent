# Yeni Modül Geliştirme Rehberi

Bu rehber, platforma yeni bir modül eklemenin adım adım nasıl yapılacağını açıklar.

---

## Modül Nedir?

Platform'daki her modül bağımsız bir Spring Boot uygulamasıdır. `platform-core`'u bağımlılık olarak alır, kendi veritabanı entity'lerini, servislerini ve controller'larını barındırır. Swagger, güvenlik ve audit altyapısı otomatik olarak devreye girer.

---

## Adım 1: Maven Modülü Oluştur

### 1.1 Klasör yapısını kur

```
platform-parent/
└── platform-yenimodul/
    ├── src/
    │   └── main/
    │       ├── java/com/platform/yenimodul/
    │       │   ├── YeniModulApplication.java
    │       │   └── config/
    │       │       └── OpenApiConfig.java
    │       └── resources/
    │           └── application.yml
    └── pom.xml
```

### 1.2 pom.xml oluştur

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" ...>
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.platform</groupId>
        <artifactId>platform-parent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>platform-yenimodul</artifactId>
    <name>Platform Yeni Modül</name>

    <dependencies>
        <!-- Zorunlu: ortak güvenlik, JWT, audit altyapısı -->
        <dependency>
            <groupId>com.platform</groupId>
            <artifactId>platform-core</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Swagger UI -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### 1.3 Ana parent pom.xml'e modülü ekle

`platform-parent/pom.xml` dosyasındaki `<modules>` bölümüne ekle:

```xml
<modules>
    <module>platform-core</module>
    <module>platform-gateway</module>
    <module>platform-module-engine</module>
    <module>platform-cbs</module>
    <module>platform-dashboard</module>
    <module>platform-yenimodul</module>  <!-- YENİ -->
</modules>
```

---

## Adım 2: Spring Boot Ana Sınıfını Yaz

```java
package com.platform.yenimodul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import com.platform.core.security.config.SecurityConfig;

@SpringBootApplication(scanBasePackages = {
    "com.platform.yenimodul",
    "com.platform.core"           // platform-core bean'lerini tarar
})
public class YeniModulApplication {
    public static void main(String[] args) {
        SpringApplication.run(YeniModulApplication.class, args);
    }
}
```

> **Önemli:** `scanBasePackages` içine hem kendi paketini hem de `com.platform.core` paketini ekle.
> Aksi hâlde JWT filtresi, SecurityConfig ve diğer core bean'ler yüklenmez.

---

## Adım 3: application.yml Yaz

```yaml
server:
  port: 8084   # Her modülün farklı bir portu olmalı

spring:
  application:
    name: platform-yenimodul
  datasource:
    url: jdbc:postgresql://localhost:5433/platformdb
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 2000ms

jwt:
  secret: 404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
  expiration: 86400000

# Swagger / OpenAPI
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    tags-sorter: alpha
    operations-sorter: method
    display-request-duration: true

logging:
  level:
    com.platform: DEBUG
```

---

## Adım 4: OpenApiConfig Sınıfı Ekle

```java
package com.platform.yenimodul.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Value("${server.port:8084}")
    private String serverPort;

    @Bean
    public OpenAPI yeniModulOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Platform Yeni Modül API")
                        .version("1.0.0")
                        .description("Yeni modülün açıklaması buraya gelir.")
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
                                .bearerFormat("JWT")));
    }
}
```

---

## Adım 5: Entity Yaz

```java
package com.platform.yenimodul.ornek.entity;

import com.platform.core.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ornek_tablo")
@Getter @Setter @NoArgsConstructor
public class OrnekEntity extends BaseEntity {

    @Column(nullable = false)
    private String ad;

    private String aciklama;

    // BaseEntity'den kalıtım yoluyla gelir:
    // - id (UUID)
    // - companyId (multi-tenant için)
    // - createdAt, updatedAt
    // - deleted (soft-delete)
}
```

> **Multi-tenant:** `BaseEntity` içindeki `companyId` alanı, her tenant'ın verisini birbirinden izole eder.
> Servis katmanında mevcut kullanıcının `companyId`'sini `SecurityContextHolder`'dan alıp sorguya eklemeyi unutma.

---

## Adım 6: Repository Yaz

```java
package com.platform.yenimodul.ornek.repository;

import com.platform.yenimodul.ornek.entity.OrnekEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface OrnekRepository extends JpaRepository<OrnekEntity, UUID> {

    // Soft-delete ve multi-tenant filtresi
    List<OrnekEntity> findByCompanyIdAndDeletedFalse(UUID companyId);

    Optional<OrnekEntity> findByIdAndDeletedFalse(UUID id);
}
```

---

## Adım 7: Servis Yaz

```java
package com.platform.yenimodul.ornek.service;

import com.platform.core.common.exception.ResourceNotFoundException;
import com.platform.core.security.model.UserPrincipal;
import com.platform.yenimodul.ornek.entity.OrnekEntity;
import com.platform.yenimodul.ornek.repository.OrnekRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrnekService {

    private final OrnekRepository ornekRepository;

    public List<OrnekEntity> getAll() {
        UUID companyId = getCurrentCompanyId();
        return ornekRepository.findByCompanyIdAndDeletedFalse(companyId);
    }

    public OrnekEntity getById(UUID id) {
        return ornekRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kayıt bulunamadı: " + id));
    }

    // Mevcut kullanıcının companyId'sini JWT token'dan alır
    private UUID getCurrentCompanyId() {
        UserPrincipal principal = (UserPrincipal)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getCompanyId();
    }
}
```

---

## Adım 8: Controller Yaz

```java
package com.platform.yenimodul.ornek.controller;

import com.platform.core.common.response.ApiResponse;
import com.platform.yenimodul.ornek.entity.OrnekEntity;
import com.platform.yenimodul.ornek.service.OrnekService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/ornekler")
@RequiredArgsConstructor
@Tag(name = "Örnek Yönetimi", description = "Örnek CRUD işlemleri")
public class OrnekController {

    private final OrnekService ornekService;

    @GetMapping
    @Operation(summary = "Tümünü listele")
    public ResponseEntity<ApiResponse<List<OrnekEntity>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(ornekService.getAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detay getir")
    public ResponseEntity<ApiResponse<OrnekEntity>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(ornekService.getById(id)));
    }
}
```

---

## Adım 9: Dockerfile Ekle

`platform-yenimodul/Dockerfile`:

```dockerfile
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S platform && adduser -S platform -G platform
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./
USER platform
EXPOSE 8084
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -q --spider http://localhost:8084/actuator/health || exit 1
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
```

---

## Adım 10: docker-compose.yml'e Ekle

`docker-compose.yml` dosyasına yeni servis bloku ekle:

```yaml
  platform-yenimodul:
    build:
      context: ./platform-yenimodul
      dockerfile: Dockerfile
    ports:
      - "8084:8084"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/platformdb
      - SPRING_DATASOURCE_USERNAME=${DB_USERNAME}
      - SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
      - SPRING_DATA_REDIS_HOST=redis
      - JWT_SECRET=${JWT_SECRET}
      - SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-dev}
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
    networks:
      - platform-network
    restart: unless-stopped
```

---

## Kontrol Listesi

Yeni modül eklerken şu adımların tamamlandığından emin ol:

- [ ] `platform-parent/pom.xml`'e `<module>` eklendi
- [ ] `pom.xml`'de `platform-core` bağımlılığı var
- [ ] `@SpringBootApplication` içinde `com.platform.core` scan ediliyor
- [ ] `application.yml`'de benzersiz port numarası kullanıldı
- [ ] `OpenApiConfig` sınıfı oluşturuldu
- [ ] `springdoc` ayarları `application.yml`'e eklendi
- [ ] Entity'ler `BaseEntity`'den türetildi (multi-tenant ve soft-delete desteği)
- [ ] Servis katmanında `companyId` filtresi uygulandı
- [ ] Controller'lara `@Tag` ve `@Operation` anotasyonları eklendi
- [ ] `Dockerfile` oluşturuldu
- [ ] `docker-compose.yml`'e servis eklendi
- [ ] En az bir unit test yazıldı

---

## Swagger Adresi

Modül çalıştırıldıktan sonra:

```
http://localhost:8084/swagger-ui.html
```
