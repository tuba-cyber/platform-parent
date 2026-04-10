package com.platform.cbs.layer.controller;

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

import com.platform.cbs.layer.dto.LayerRequest;
import com.platform.cbs.layer.dto.LayerResponse;
import com.platform.cbs.layer.service.LayerService;
import com.platform.core.common.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/layers")
@RequiredArgsConstructor
@Tag(name = "CBS - Katman Yönetimi", description = "Coğrafi bilgi sistemi katmanlarının (VECTOR, RASTER, WMS, WFS) yönetimi")
public class LayerController {

    private final LayerService layerService;

    @GetMapping
    @PreAuthorize("hasAuthority('CBS_VIEW')")
    @Operation(summary = "Tüm katmanları listele", description = "Tenant'a ait tüm CBS katmanlarını listeler. CBS_VIEW yetkisi gerektirir.")
    public ResponseEntity<ApiResponse<List<LayerResponse>>> getAll() {
        return ResponseEntity.ok(
            ApiResponse.success(layerService.getAll())
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CBS_VIEW')")
    @Operation(summary = "Katman detayı", description = "ID ile tek bir CBS katmanının detayını getirir. CBS_VIEW yetkisi gerektirir.")
    public ResponseEntity<ApiResponse<LayerResponse>> getById(
            @PathVariable("id") UUID id) {
        return ResponseEntity.ok(
            ApiResponse.success(layerService.getById(id))
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CBS_EDIT')")
    @Operation(summary = "Katman oluştur", description = "Yeni bir CBS katmanı oluşturur. CBS_EDIT yetkisi gerektirir.")
    public ResponseEntity<ApiResponse<LayerResponse>> create(
            @Valid @RequestBody LayerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(layerService.create(request), "Katman oluşturuldu")
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CBS_EDIT')")
    @Operation(summary = "Katman güncelle", description = "Mevcut CBS katmanının bilgilerini günceller. CBS_EDIT yetkisi gerektirir.")
    public ResponseEntity<ApiResponse<LayerResponse>> update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody LayerRequest request) {
        return ResponseEntity.ok(
            ApiResponse.success(layerService.update(id, request), "Katman güncellendi")
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CBS_EDIT')")
    @Operation(summary = "Katman sil", description = "CBS katmanını soft-delete ile siler. CBS_EDIT yetkisi gerektirir.")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable("id") UUID id) {
        layerService.delete(id);
        return ResponseEntity.ok(
            ApiResponse.success(null, "Katman silindi")
        );
    }
}
