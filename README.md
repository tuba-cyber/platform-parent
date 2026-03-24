# 🏢 White-Label Kurumsal Platform

Spring Boot 3 tabanlı, modüler mimariye sahip çok kiracılı (multi-tenant) 
bir white-label kurumsal web uygulaması. Her modül bağımsız bir Spring Boot 
servisi olarak çalışır ve ortak güvenlik altyapısını paylaşır.

>  Aktif geliştirme aşamasındadır.

---

##  Mimari
```
┌─────────────────────────────────────────────────┐
│                   CLIENT                        │
│         (React + TypeScript - Planlanan)        │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│           platform-gateway (8080)               │
│     Auth · JWT · Kullanıcı · Şirket Yönetimi    │
└──────┬───────────────────────────────┬──────────┘
       │                               │
┌──────▼──────────┐         ┌──────────▼─────────┐
│ platform-module │         │   platform-cbs     │
│  -engine (8081) │         │      (8082)        │
│ Modül · Bölüm   │         │ Harita · Katman    │
│ Ekran · Navigas.│         │ PostGIS · GeoJSON  │
└─────────────────┘         └────────────────────┘
       │                               │
┌──────▼───────────────────────────────▼─────────┐
│              PostgreSQL 16 + PostGIS            │
└─────────────────────────────────────────────────┘
```

---

##  Modüller

| Modül | Port | Açıklama |
|-------|------|----------|
| `platform-core` | - | Ortak güvenlik, entity ve exception altyapısı |
| `platform-gateway` | 8080 | Auth, JWT, kullanıcı ve şirket yönetimi |
| `platform-module-engine` | 8081 | Dinamik modül, ekran ve navigasyon motoru |
| `platform-cbs` | 8082 | CBS modülü, PostGIS destekli harita katmanları |
| `platform-dashboard` | 8083 | Dashboard ve raporlama (geliştiriliyor) |

---

##  Güvenlik Mimarisi

- JWT tabanlı stateless kimlik doğrulama
- Rol ve izin bazlı yetkilendirme (RBAC)
- Token içine `companyId`, `roles` ve `permissions` gömülerek
  her istek kendi şirket bağlamını taşır
- `@PreAuthorize` ile endpoint bazlı erişim kontrolü
```java
// Her serviste şirket izolasyonu bu kadar basit:
String companyId = principal.getCompanyId();
layerRepository.findByCompanyIdAndActive(companyId, true);
```

---

##  CBS Modülü (PostGIS)

- PostGIS 3.5 + Hibernate Spatial entegrasyonu
- Nokta, çizgi, poligon geometri desteği
- GeoJSON formatında veri alışverişi
- Bbox ve yakınlık sorguları

---

##  Kullanılan Teknolojiler

**Backend**
- Java 21
- Spring Boot 3.2.5
- Spring Security + JWT
- Spring Data JPA + Hibernate Spatial
- PostgreSQL 16 + PostGIS 3.5
- Maven Multi-Module

**Planlanan**
- React + TypeScript + Module Federation
- Apache ECharts
- GeoServer + MapLibre GL JS
- Docker + Docker Compose
- Redis

---

##  Nasıl Çalıştırılır?

### Gereksinimler
- Java 21
- PostgreSQL 16 + PostGIS 3.5
- Maven 3.9+

### Veritabanı Kurulumu
```sql
CREATE DATABASE platformdb;
\c platformdb
CREATE EXTENSION IF NOT EXISTS postgis;
```

### Servisleri Başlat

Her modül bağımsız olarak çalıştırılır:
```bash
# Gateway (önce bu başlatılmalı)
cd platform-gateway
mvn spring-boot:run

# Module Engine
cd platform-module-engine
mvn spring-boot:run

# CBS
cd platform-cbs
mvn spring-boot:run
```

### Varsayılan Kullanıcılar

| Kullanıcı | Şifre | Rol |
|-----------|-------|-----|
| admin | admin123 | ADMIN |
| user | user123 | USER |

---

## 📡 API Endpoints

### Auth (8080)
```
POST /api/v1/auth/login
POST /api/v1/auth/register  
POST /api/v1/auth/refresh
```

### Module Engine (8081)
```
GET/POST   /api/v1/modules
GET/PUT/DELETE /api/v1/modules/{id}
GET/POST   /api/v1/modules/{moduleId}/sections
GET/POST   /api/v1/sections/{sectionId}/screens
GET        /api/v1/navigation
```

### CBS (8082)
```
GET/POST   /api/v1/layers
GET/PUT/DELETE /api/v1/layers/{id}
GET/POST   /api/v1/features
GET        /api/v1/features/layer/{layerId}
GET        /api/v1/features/layer/{layerId}/bbox
GET        /api/v1/features/layer/{layerId}/nearby
```

---

## 📋 Geliştirme Durumu

- [x] platform-core — Ortak altyapı
- [x] platform-gateway — Auth & kullanıcı yönetimi  
- [x] platform-module-engine — Modül motoru
- [x] platform-cbs — CBS & harita modülü
- [ ] platform-dashboard — Dashboard & raporlama
- [ ] Frontend — React + TypeScript
- [ ] Docker & CI/CD
- [ ] Swagger / OpenAPI dokümantasyonu

---

##  Geliştirici

Aktif olarak geliştirilmekte olan bu proje, kurumsal düzeyde 
ölçeklenebilir bir platform altyapısı oluşturma amacıyla başlatılmıştır.
