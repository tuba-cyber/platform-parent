package com.platform.dashboard.widget.controller;

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
import com.platform.dashboard.widget.dto.WidgetRequest;
import com.platform.dashboard.widget.dto.WidgetResponse;
import com.platform.dashboard.widget.service.WidgetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/widgets")
@RequiredArgsConstructor
@Tag(name = "Widget Yönetimi", description = "Dashboard widget'larının yönetimi. Widget türleri: COUNTER, BAR_CHART, LINE_CHART, PIE_CHART, MAP, TABLE")
public class WidgetController {

    private final WidgetService widgetService;

    @GetMapping
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @Operation(summary = "Tüm widget'ları listele", description = "Tenant'a ait tüm widget tanımlarını listeler.")
    public ResponseEntity<ApiResponse<List<WidgetResponse>>> getAll() {
        return ResponseEntity.ok(
            ApiResponse.success(widgetService.getAll())
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @Operation(summary = "Widget detayı", description = "ID ile tek bir widget'ın detayını getirir.")
    public ResponseEntity<ApiResponse<WidgetResponse>> getById(
            @PathVariable("id") UUID id) {
        return ResponseEntity.ok(
            ApiResponse.success(widgetService.getById(id))
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @Operation(summary = "Widget oluştur",
               description = "Yeni bir widget tanımı oluşturur. Veri kaynağı (dataSourceType): SQL, REST_API, CBS_LAYER, STATIC")
    public ResponseEntity<ApiResponse<WidgetResponse>> create(
            @Valid @RequestBody WidgetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(
                widgetService.create(request), "Widget oluşturuldu"
            )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @Operation(summary = "Widget güncelle", description = "Mevcut widget'ın bilgilerini günceller.")
    public ResponseEntity<ApiResponse<WidgetResponse>> update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody WidgetRequest request) {
        return ResponseEntity.ok(
            ApiResponse.success(
                widgetService.update(id, request), "Widget güncellendi"
            )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @Operation(summary = "Widget sil", description = "Widget tanımını siler.")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable("id") UUID id) {
        widgetService.delete(id);
        return ResponseEntity.ok(
            ApiResponse.success(null, "Widget silindi")
        );
    }
}
