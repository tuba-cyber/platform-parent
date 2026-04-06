# AŞAMA 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

COPY pom.xml .
COPY platform-core/pom.xml platform-core/
COPY platform-gateway/pom.xml platform-gateway/
COPY platform-module-engine/pom.xml platform-module-engine/
COPY platform-cbs/pom.xml platform-cbs/
COPY platform-dashboard/pom.xml platform-dashboard/

RUN apk add --no-cache maven && \
    mvn dependency:go-offline -pl platform-core,platform-cbs -am -q

COPY platform-core/src platform-core/src
COPY platform-cbs/src platform-cbs/src

RUN mvn clean package -pl platform-core,platform-cbs -am -DskipTests -q

# AŞAMA 2: Runtime
FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app

COPY --from=builder /build/platform-cbs/target/platform-cbs-*.jar app.jar

EXPOSE 8082

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -q --spider http://localhost:8082/actuator/health || exit 1

ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
