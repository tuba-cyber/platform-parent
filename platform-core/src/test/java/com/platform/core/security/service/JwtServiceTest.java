package com.platform.core.security.service;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970TestKey1234567890";
    private static final long JWT_EXPIRATION = 86400000L;
    private static final long REFRESH_EXPIRATION = 604800000L;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();
        setField(jwtService, "secretKey", SECRET_KEY);
        setField(jwtService, "jwtExpiration", JWT_EXPIRATION);
        setField(jwtService, "refreshExpiration", REFRESH_EXPIRATION);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    @DisplayName("Token başarıyla üretilmeli")
    void shouldGenerateToken() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken("testuser", userId, "company1",
                List.of("ADMIN"), List.of("USER_READ", "USER_WRITE"));

        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("Token'dan username çıkarılabilmeli")
    void shouldExtractUsername() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken("testuser", userId, "company1",
                List.of("ADMIN"), List.of("USER_READ"));

        String username = jwtService.extractUsername(token);
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Token'dan userId çıkarılabilmeli")
    void shouldExtractUserId() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken("testuser", userId, "company1",
                List.of("ADMIN"), List.of("USER_READ"));

        UUID extractedUserId = jwtService.extractUserId(token);
        assertThat(extractedUserId).isEqualTo(userId);
    }

    @Test
    @DisplayName("Token'dan companyId çıkarılabilmeli")
    void shouldExtractCompanyId() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken("testuser", userId, "company1",
                List.of("ADMIN"), List.of("USER_READ"));

        String companyId = jwtService.extractCompanyId(token);
        assertThat(companyId).isEqualTo("company1");
    }

    @Test
    @DisplayName("Token'dan roller çıkarılabilmeli")
    void shouldExtractRoles() {
        UUID userId = UUID.randomUUID();
        List<String> roles = List.of("ADMIN", "USER");
        String token = jwtService.generateToken("testuser", userId, "company1",
                roles, List.of("USER_READ"));

        List<String> extractedRoles = jwtService.extractRoles(token);
        assertThat(extractedRoles).containsExactlyInAnyOrderElementsOf(roles);
    }

    @Test
    @DisplayName("Token'dan yetkiler çıkarılabilmeli")
    void shouldExtractPermissions() {
        UUID userId = UUID.randomUUID();
        List<String> permissions = List.of("USER_READ", "USER_WRITE", "MODULE_MANAGE");
        String token = jwtService.generateToken("testuser", userId, "company1",
                List.of("ADMIN"), permissions);

        List<String> extractedPermissions = jwtService.extractPermissions(token);
        assertThat(extractedPermissions).containsExactlyInAnyOrderElementsOf(permissions);
    }

    @Test
    @DisplayName("Geçerli token doğrulanabilmeli")
    void shouldValidateToken() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken("testuser", userId, "company1",
                List.of("ADMIN"), List.of("USER_READ"));

        boolean isValid = jwtService.isTokenValid(token, "testuser");
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Yanlış kullanıcı ile token geçersiz olmalı")
    void shouldInvalidateTokenWithWrongUsername() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken("testuser", userId, "company1",
                List.of("ADMIN"), List.of("USER_READ"));

        boolean isValid = jwtService.isTokenValid(token, "wronguser");
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Refresh token başarıyla üretilmeli")
    void shouldGenerateRefreshToken() {
        String refreshToken = jwtService.generateRefreshToken("testuser");

        assertThat(refreshToken).isNotNull().isNotEmpty();
        String username = jwtService.extractUsername(refreshToken);
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Token süresi alınabilmeli")
    void shouldGetExpirationTime() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken("testuser", userId, "company1",
                List.of("ADMIN"), List.of("USER_READ"));

        long expirationTime = jwtService.getExpirationTime(token);
        assertThat(expirationTime).isPositive();
        assertThat(expirationTime).isLessThanOrEqualTo(JWT_EXPIRATION);
    }

    @Test
    @DisplayName("Süresi dolmuş token geçersiz olmalı")
    void shouldInvalidateExpiredToken() throws Exception {
        // Negatif süre → token oluşturulur oluşturulmaz süresi dolmuş sayılır
        setField(jwtService, "jwtExpiration", -1000L);

        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken("testuser", userId, "company1",
                List.of("ADMIN"), List.of("USER_READ"));

        // JJWT 0.12.x: süresi dolmuş token parse edilemez, ExpiredJwtException fırlatır
        // Bu davranış doğrudur — false dönmek yerine exception fırlatıyor
        assertThatThrownBy(() -> jwtService.isTokenValid(token, "testuser"))
                .isInstanceOf(RuntimeException.class);

        // Geri yükle
        setField(jwtService, "jwtExpiration", JWT_EXPIRATION);
    }

    @Test
    @DisplayName("Geçersiz token'da getExpirationTime 0 dönmeli")
    void shouldReturnZeroForInvalidTokenExpiration() {
        long expiration = jwtService.getExpirationTime("invalid.token.here");
        assertThat(expiration).isEqualTo(0);
    }
}
