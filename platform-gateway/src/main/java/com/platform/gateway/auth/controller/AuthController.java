package com.platform.gateway.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.core.common.response.ApiResponse;
import com.platform.gateway.auth.dto.LoginRequest;
import com.platform.gateway.auth.dto.LoginResponse;
import com.platform.gateway.auth.dto.RefreshTokenRequest;
import com.platform.gateway.auth.dto.RegisterRequest;
import com.platform.gateway.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Kimlik Doğrulama", description = "Giriş, kayıt, token yenileme ve çıkış işlemleri")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
        summary = "Kullanıcı girişi",
        description = "Kullanıcı adı ve şifre ile giriş yapar. Başarılı olursa access token ve refresh token döner."
    )
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Giriş başarılı"));
    }

    @PostMapping("/register")
    @Operation(
        summary = "Yeni kullanıcı kaydı",
        description = "Yeni kullanıcı hesabı oluşturur ve otomatik olarak giriş yapar."
    )
    public ResponseEntity<ApiResponse<LoginResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        LoginResponse response = authService.register(request);
        return ResponseEntity.ok(
            ApiResponse.success(response, "Kayıt başarılı")
        );
    }

    @PostMapping("/refresh")
    @Operation(
        summary = "Token yenileme",
        description = "Süresi dolmak üzere olan access token'ı refresh token ile yeniler."
    )
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(
            ApiResponse.success(response, "Token yenilendi")
        );
    }

    @PostMapping("/logout")
    @Operation(
        summary = "Çıkış yap",
        description = "Mevcut token'ı geçersiz kılar (Redis blacklist'e ekler).",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        authService.logout(token);
        return ResponseEntity.ok(
            ApiResponse.success(null, "Çıkış yapıldı")
        );
    }
}
