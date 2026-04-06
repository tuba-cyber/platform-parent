-- ============================================================
-- PostgreSQL başlangıç betiği
-- Docker container ilk kez başladığında çalışır
-- ============================================================

-- PostGIS uzantısını aktif et (CBS modülü için gerekli)
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS postgis_topology;

-- UUID üretimi için
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Türkçe karakter desteği için
SET client_encoding = 'UTF8';
