package com.platform.moduleengine.navigation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.core.common.response.ApiResponse;
import com.platform.moduleengine.navigation.dto.NavigationResponse;
import com.platform.moduleengine.navigation.service.NavigationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/navigation")
@RequiredArgsConstructor
@Tag(name = "Navigasyon", description = "Giriş yapan kullanıcının yetkisine göre navigasyon ağacını döner")
public class NavigationController {

    private final NavigationService navigationService;

    @GetMapping
    @Operation(summary = "Kullanıcı navigasyonunu getir",
               description = "Oturum açmış kullanıcının rolüne ve modül yetkilerine göre filtrelenmiş navigasyon ağacını döner.")
    public ResponseEntity<ApiResponse<List<NavigationResponse>>> getNavigation() {
        return ResponseEntity.ok(
            ApiResponse.success(
                navigationService.getNavigationForCurrentUser()
            )
        );
    }
}