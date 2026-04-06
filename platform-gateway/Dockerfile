# ─────────────────────────────────────────
# AŞAMA 1: Build (derleme)
# Maven ile projeyi derleyip JAR dosyası oluşturur
# ─────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Önce sadece pom.xml dosyalarını kopyala (bağımlılık cache'i için)
COPY pom.xml .
COPY platform-core/pom.xml platform-core/
COPY platform-gateway/pom.xml platform-gateway/
COPY platform-module-engine/pom.xml platform-module-engine/
COPY platform-cbs/pom.xml platform-cbs/
COPY platform-dashboard/pom.xml platform-dashboard/

# Maven wrapper veya mvn ile bağımlılıkları indir
RUN apk add --no-cache maven && \
    mvn dependency:go-offline -pl platform-core,platform-gateway -am -q

# Kaynak kodları kopyala
COPY platform-core/src platform-core/src
COPY platform-gateway/src platform-gateway/src

# Derle (test atla - testler CI'da ayrı çalışır)
RUN mvn clean package -pl platform-core,platform-gateway -am -DskipTests -q

# ─────────────────────────────────────────
# AŞAMA 2: Runtime (çalışma ortamı)
# Sadece JRE kullan, daha küçük image boyutu
# ─────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

# Güvenlik: root olmayan kullanıcı
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app

# JAR dosyasını kopyala
COPY --from=builder /build/platform-gateway/target/platform-gateway-*.jar app.jar

# Uygulama portu
EXPOSE 8080

# Sağlık kontrolü — Docker bu endpoint'i ping'ler
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

# Başlatma komutu
# SPRING_PROFILES_ACTIVE ortam değişkeni ile profil seçilir (dev/prod)
ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
