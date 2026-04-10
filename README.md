# Platform Parent — Modüler White-Label Kurumsal Platform

Spring Boot 3 tabanlı, modüler monolith mimarisiyle geliştirilmiş çok kiracılı (multi-tenant) white-label kurumsal uygulama.

---

## Mimari Genel Bakış

```
platform-parent/
├── platform-core/          # Ortak güvenlik, JWT, bildirim, dosya yükleme
├── platform-gateway/       # Kimlik doğrulama, kullanıcı/rol yönetimi  (port: 8080)
├── platform-module-engine/ # Modül, ekran, bölüm, navigasyon yönetimi  (port: 8081)
├── platform-cbs/           # Coğrafi Bilgi Sistemi (CBS/GIS)            (port: 8082)
└── platform-dashboard/     # Dashboard, widget ve rapor yönetimi        (port: 8083)
```

Her modül bağımsız bir Spring Boot servisi olarak çalışır, ortak güvenlik altyapısını (`platform-core`) paylaşır ve aynı PostgreSQL veritabanını kullanır.

---

## Teknoloji Yığını

| Katman | Teknoloji |
|--------|-----------|
| Backend | Spring Boot 3.2.5, Java 21 |
| Güvenlik | Spring Security, JWT (JJWT 0.12.5) |
| Veritabanı | PostgreSQL 16 + PostGIS 3.4 |
| Cache / Session | Redis 7.2 |
| ORM | Spring Data JPA + Hibernate Spatial |
| Coğrafi | PostGIS, JTS Topology Suite |
| Build | Maven (multi-module) |
| API Docs | SpringDoc OpenAPI 3 (Swagger UI) |
| Test | JUnit 5, Mockito, AssertJ, H2 |
| Code Coverage | JaCoCo |
| Container | Docker + Docker Compose |
| CI/CD | GitHub Actions |

---

## Hızlı Başlangıç

### Gereksinimler

- Java 21+
- Maven 3.9+
- Docker & Docker Compose

### 1. Repoyu klonla

```bash
git clone https://github.com/tuba-cyber/platform-parent.git
cd platform-parent
```

### 2. Ortam değişkenlerini ayarla

```bash
cp .env.example .env
# .env dosyasını düzenleyerek gerekli değerleri gir
```

### 3. Docker ile başlat (veritabanı + Redis)

```bash
docker-compose up -d postgres redis
```

### 4. Uygulamayı derle ve çalıştır

```bash
# Tüm modülleri derle
mvn clean install -DskipTests

# Her modülü ayrı terminalde başlat:
cd platform-gateway    && mvn spring-boot:run
cd platform-module-engine && mvn spring-boot:run
cd platform-cbs        && mvn spring-boot:run
cd platform-dashboard  && mvn spring-boot:run
```

---

## API Dokümantasyonu (Swagger UI)

Her modül kendi Swagger arayüzüne sahiptir:

| Modül | Swagger UI | OpenAPI JSON |
|-------|-----------|--------------|
| Gateway | http://localhost:8080/swagger-ui.html | http://localhost:8080/v3/api-docs |
| Module Engine | http://localhost:8081/swagger-ui.html | http://localhost:8081/v3/api-docs |
| CBS | http://localhost:8082/swagger-ui.html | http://localhost:8082/v3/api-docs |
| Dashboard | http://localhost:8083/swagger-ui.html | http://localhost:8083/v3/api-docs |

### Swagger'da Nasıl Test Edilir?

1. Önce `http://localhost:8080/swagger-ui.html` adresini aç
2. `/api/v1/auth/login` endpoint'ini genişlet
3. Test kullanıcısı ile giriş yap:
   ```json
   { "username": "admin", "password": "Admin123!" }
   ```
4. Dönen `accessToken` değerini kopyala
5. Sağ üstteki **Authorize** butonuna tıkla
6. Token değerini yapıştır (Bearer öneki olmadan)
7. Artık tüm korumalı endpoint'leri test edebilirsin

---

## Modüller ve API Endpoint'leri

### platform-gateway (port: 8080)

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| POST | `/api/v1/auth/login` | Kullanıcı girişi |
| POST | `/api/v1/auth/register` | Yeni kullanıcı kaydı |
| POST | `/api/v1/auth/refresh` | Token yenileme |
| POST | `/api/v1/auth/logout` | Çıkış (token blacklist) |

### platform-module-engine (port: 8081)

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET | `/api/v1/modules` | Modülleri listele |
| POST | `/api/v1/modules` | Modül oluştur |
| PATCH | `/api/v1/modules/{id}/toggle` | Aktif/pasif değiştir |
| GET | `/api/v1/navigation` | Kullanıcı navigasyon ağacı |
| GET/POST | `/api/v1/modules/{id}/sections` | Bölüm yönetimi |
| GET/POST | `/api/v1/sections/{id}/screens` | Ekran yönetimi |

### platform-cbs (port: 8082)

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET/POST | `/api/v1/layers` | Katman yönetimi |
| GET/POST | `/api/v1/features` | Coğrafi özellik yönetimi |
| GET | `/api/v1/features/layer/{id}/bbox` | Bounding box sorgusu |
| GET | `/api/v1/features/layer/{id}/nearby` | Yakın özellik sorgusu |

### platform-dashboard (port: 8083)

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET/POST | `/api/v1/dashboards` | Dashboard yönetimi |
| GET | `/api/v1/dashboards/default` | Varsayılan dashboard |
| POST | `/api/v1/dashboards/{id}/widgets` | Widget ekle |
| GET/POST | `/api/v1/widgets` | Widget tanımları |
| GET/POST | `/api/v1/reports` | Rapor tanımları |

---

## Güvenlik Modeli

### Rol ve Yetki Yapısı

Sistem **RBAC (Role-Based Access Control)** kullanır. Her kullanıcının bir rolü, her rolün bir yetki listesi vardır.

Örnek yetki kodları:

| Yetki | Açıklama |
|-------|----------|
| `MODULE_VIEW` | Modülleri görüntüleme |
| `MODULE_EDIT` | Modül oluşturma/güncelleme/silme |
| `CBS_VIEW` | CBS katmanlarını görüntüleme |
| `CBS_EDIT` | CBS verisi oluşturma/güncelleme |
| `DASHBOARD_VIEW` | Dashboard erişimi |

### Multi-Tenant Yapı

Her şirket (tenant) birbirinden izole verilere sahiptir. Tenant ayrımı `companyId` alanıyla yapılır ve JWT token'ın içine gömülür.

---

## Testleri Çalıştırma

```bash
# Tüm testleri çalıştır
mvn test

# Kod kapsama raporu oluştur
mvn test jacoco:report
# Rapor: target/site/jacoco/index.html

# Tek modül testi
cd platform-gateway
mvn test
```

---

## Docker Compose ile Tam Başlatma

```bash
# Tüm servisleri başlat
docker-compose up -d

# Servis durumlarını kontrol et
docker-compose ps

# Logları takip et
docker-compose logs -f platform-gateway

# Durdur
docker-compose down
```

---

## Proje Geliştirme Aşamaları

- [x] Mimari & Tasarım Kararları
- [x] Temel Altyapı (Spring Boot, PostgreSQL, Redis, JWT)
- [x] Kimlik & Yetki Modülü (Auth & RBAC)
- [x] Modül Yönetim Sistemi
- [x] Ekran Şablon Sistemi
- [x] CBS Modülü (PostGIS, GeoJSON)
- [x] Dashboard & Raporlama Modülü
- [x] Yardımcı Servisler (Bildirim, Dosya, Audit)
- [x] Test & Kalite (JUnit 5, JaCoCo)
- [x] DevOps & Deployment (Docker, GitHub Actions)
- [x] Dokümantasyon (Swagger/OpenAPI, README)

---

## Katkı Rehberi

Yeni modül eklemek için `docs/MODULE_GUIDE.md` dosyasına bakınız.

---

## Lisans

Bu proje tescilli yazılımdır. Tüm hakları saklıdır.
