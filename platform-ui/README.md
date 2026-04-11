# Platform UI — Module Federation Frontend

React 18 + TypeScript + Webpack 5 Module Federation ile geliştirilmiş micro-frontend mimarisi.

## Yapı

| Uygulama         | Port | Açıklama                              |
|------------------|------|---------------------------------------|
| `shell`          | 3000 | Host app — layout, auth, routing      |
| `platform-co-ui` | 3001 | Şirket & şube yönetimi               |
| `platform-hr-ui` | 3002 | Çalışan, departman, pozisyon yönetimi |

## Kurulum & Çalıştırma

Her uygulama için ayrı terminal açılmalı:

```bash
# 1. platform-co-ui
cd platform-ui/platform-co-ui
npm install
npm start

# 2. platform-hr-ui
cd platform-ui/platform-hr-ui
npm install
npm start

# 3. Shell (son açılmalı — remote'ların hazır olması gerekir)
cd platform-ui/shell
npm install
npm start
```

Tarayıcıda: **http://localhost:3000**

## Giriş Bilgileri

- Kullanıcı: `admin`
- Şifre: `admin123`
