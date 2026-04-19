# CLAUDE.md — Proje Rehberi

Bu dosya, her sohbet başında Claude tarafından okunmalıdır.
Projeye nereden devam edeceğimizi belirler ve çalışma kurallarımızı tanımlar.

---

## 🎯 Çalışma Kuralları

### Öğretici Mod
- Her kod bloğunu yaz, sonra **ne yaptığını ve neden böyle yaptığını** açıkla.
- Yeni bir kavram veya pattern kullandığında mutlaka "Bu nedir, neden kullandık?" diye anlat.
- Karşılaştırmalı açıkla: "Bunu şöyle de yapabilirdik ama şu yüzden bu yolu seçtik."
- Adım adım ilerle — tek seferde çok şey yazma, her adımı sindirmemi bekle.

### Sohbet Başlangıcı (Her Zaman Yap)
Her yeni sohbetin en başında şunu yap:
1. Bu dosyayı oku.
2. Hangi aşamada olduğumuzu ve son kaldığımız yeri söyle.
3. "Kaldığımız yerden devam edelim mi, yoksa farklı bir şeyle mi başlayalım?" diye sor.

### İlerleme Güncellemesi
Bir alt görev tamamlandığında:
- İlgili kutucuğu `[x]` olarak güncelle.
- "MEVCUT DURUM" bölümünü güncelle.
- Bir sonraki adımı belirt.

---

## 📊 MEVCUT DURUM

```
Son güncelleme : [ilk oturum - henüz başlanmadı]
Aktif aşama   : Aşama 1 — Mimari & Tasarım Kararları
Son yapılan   : —
Sıradaki adım : Mimari dokümanı oluşturmak
```

---

## 📋 Proje Geliştirme Aşamaları

### Aşama 1 — 📐 Mimari & Tasarım Kararları
- [ ] Mimari dokümanın oluşturulması
- [ ] Veritabanı şeması tasarımı
- [ ] API kontrat tasarımı (endpoint listesi)
- [ ] Güvenlik modelinin belirlenmesi (roller, yetkiler)
- [ ] Modül yapısının ve ilişkilerinin netleştirilmesi

### Aşama 2 — 🏗️ Temel Altyapı (Core Infrastructure)
- [ ] Spring Boot proje iskeleti ve modül yapısı
- [ ] PostgreSQL + PostGIS kurulum ve konfigürasyonu
- [ ] Spring Security + JWT auth altyapısı
- [ ] Merkezi hata yönetimi, loglama
- [ ] Docker / Docker Compose ortamı

### Aşama 3 — 🔐 Kimlik & Yetki Modülü (Auth & RBAC)
- [ ] Kullanıcı, rol ve yetki yönetimi
- [ ] Modül bazlı yetkilendirme
- [ ] Oturum yönetimi (token yenileme, çıkış)
- [ ] Şirket/tenant yönetimi (white-label için)

### Aşama 4 — 🧩 Modül Yönetim Sistemi
- [ ] Modül kayıt ve konfigürasyon sistemi
- [ ] Modüller arası iletişim altyapısı
- [ ] Modül aktif/pasif yönetimi
- [ ] Bölüm ve ekran tanım sistemi

### Aşama 5 — 🖥️ Ekran Şablon Sistemi (Frontend Core)
- [ ] React + TypeScript + Module Federation kurulumu
- [ ] JSON tabanlı ekran şablon motoru
- [ ] Temel UI bileşen kütüphanesi (tablo, form, buton, kart vb.)
- [ ] Modüller arası navigasyon sistemi
- [ ] Tema / white-label görünüm sistemi

### Aşama 6 — 🗺️ CBS Modülü
- [ ] GeoServer kurulum ve entegrasyonu
- [ ] PostGIS katman ve feature yönetimi (CRUD)
- [ ] MapLibre GL JS harita bileşeni
- [ ] Çizim araçları (nokta, çizgi, alan, yol)
- [ ] Katman yönetim paneli
- [ ] Modül bazlı katman filtreleme

### Aşama 7 — 📊 Dashboard & Raporlama Modülü
- [ ] Dashboard şablon sistemi
- [ ] Grafik bileşenleri (Apache ECharts)
- [ ] CBS verilerinin grafikleştirilmesi (choropleth vb.)
- [ ] Dinamik sorgu / filtre sistemi
- [ ] Rapor export (PDF, Excel)

### Aşama 8 — 🔔 Yardımcı Servisler
- [ ] Bildirim sistemi (in-app, e-posta)
- [ ] Dosya yükleme / medya yönetimi
- [ ] Audit log (kim ne zaman ne yaptı)
- [ ] Cache yönetimi (Redis)

### Aşama 9 — 🧪 Test & Kalite
- [ ] Unit testler (JUnit + Mockito)
- [ ] Entegrasyon testleri
- [ ] Frontend component testleri
- [ ] API testleri (Postman / Rest Assured)
- [ ] Güvenlik testleri

### Aşama 10 — 🚀 DevOps & Deployment
- [ ] CI/CD pipeline (GitHub Actions veya Jenkins)
- [ ] Docker image optimizasyonu
- [ ] Ortam yönetimi (dev, test, prod)
- [ ] Monitoring & alerting (Prometheus + Grafana)
- [ ] Yedekleme stratejisi

### Aşama 11 — 📝 Dokümantasyon
- [ ] API dokümantasyonu (Swagger / OpenAPI)
- [ ] Geliştirici rehberi
- [ ] Kullanıcı kılavuzu
- [ ] Modül geliştirme rehberi (yeni modül nasıl eklenir)

---

## 🗒️ Oturum Notları

Her oturum sonunda buraya kısa not düş.

### Oturum 1
_Henüz başlanmadı._

---

## ⚙️ Teknik Bağlam

Buraya proje hakkında teknik detaylar eklenecek (Java sürümü, bağımlılıklar, özel kararlar).
İlk oturumda mevcut proje incelendikten sonra doldurulacak.
