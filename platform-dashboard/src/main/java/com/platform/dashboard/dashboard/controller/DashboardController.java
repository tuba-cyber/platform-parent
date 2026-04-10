package com.platform.dashboard.dashboard.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.core.common.response.ApiResponse;
import com.platform.dashboard.dashboard.dto.DashboardRequest;
import com.platform.dashboard.dashboard.dto.DashboardResponse;
import com.platform.dashboard.dashboard.dto.DashboardWidgetRequest;
import com.platform.dashboard.dashboard.service.DashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/dashboards")
@RequiredArgsConstructor
@Tag(name = "Dashboard Yönetimi", description = "Dashboard oluşturma, güncelleme ve widget yönetimi")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @Operation(summary = "Tüm dashboard'ları listele", description = "Tenant'a ait tüm dashboard'ları listeler.")
    public ResponseEntity<ApiResponse<List<DashboardResponse>>> getAll() {
        return ResponseEntity.ok(
            ApiResponse.success(dashboardService.getAll())
        );
    }

    @GetMapping("/default")
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @Operation(summary = "Varsayılan dashboard", description = "Tenant'ın varsayılan (default) dashboard'unu getirir.")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDefault() {
        return ResponseEntity.ok(
            ApiResponse.success(dashboardService.getDefault())
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @Operation(summary = "Dashboard detayı", description = "ID ile tek bir dashboard'un widget'larıyla birlikte detayını getirir.")
    public ResponseEntity<ApiResponse<DashboardResponse>> getById(
            @PathVariable("id") UUID id) {
        return ResponseEntity.ok(
            ApiResponse.success(dashboardService.getById(id))
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @Operation(summary = "Dashboard oluştur", description = "Yeni bir dashboard oluşturur.")
    public ResponseEntity<ApiResponse<DashboardResponse>> create(
            @Valid @RequestBody DashboardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(
                dashboardService.create(request), "Dashboard oluşturuldu"
            )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @Operation(summary = "Dashboard güncelle", description = "Mevcut dashboard'un bilgilerini günceller.")
    public ResponseEntity<ApiResponse<DashboardResponse>> update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody DashboardRequest request) {
        return ResponseEntity.ok(
            ApiResponse.success(
                dashboardService.update(id, request), "Dashboard güncellendi"
            )
        );
    }

    @PostMapping("/{id}/widgets")
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @Operation(summary = "Dashboard'a widget ekle", description = "Mevcut dashboard'a yeni bir widget bağlar.")
    public ResponseEntity<ApiResponse<DashboardResponse>> addWidget(
            @PathVariable("id") UUID id,
            @Valid @RequestBody DashboardWidgetRequest request) {
        return ResponseEntity.ok(
            ApiResponse.success(
                dashboardService.addWidget(id, request),
                "Widget eklendi"
            )
        );
    }

    @DeleteMapping("/{id}/widgets/{widgetId}")
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @Operation(summary = "Dashboard'dan widget kaldır", description = "Dashboard ile widget arasındaki bağlantıyı kaldırır.")
    public ResponseEntity<ApiResponse<Void>> removeWidget(
            @PathVariable("id") UUID id,
            @PathVariable("widgetId") UUID widgetId) {
        dashboardService.removeWidget(id, widgetId);
        return ResponseEntity.ok(
            ApiResponse.success(null, "Widget kaldırıldı")
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @Operation(summary = "Dashboard sil", description = "Dashboard'u soft-delete ile siler.")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable("id") UUID id) {
        dashboardService.delete(id);
        return ResponseEntity.ok(
            ApiResponse.success(null, "Dashboard silindi")
        );
    }
}
