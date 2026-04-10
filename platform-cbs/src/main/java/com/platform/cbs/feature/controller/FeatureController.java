package com.platform.cbs.feature.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.platform.cbs.feature.dto.FeatureRequest;
import com.platform.cbs.feature.dto.FeatureResponse;
import com.platform.cbs.feature.dto.GeoJsonFeatureCollection;
import com.platform.cbs.feature.service.FeatureService;
import com.platform.core.common.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/features")
@RequiredArgsConstructor
@Tag(name = "CBS - Coğrafi Özellik Yönetimi", description = "GeoJSON formatında nokta, çizgi ve alan (polygon) özelliklerinin (feature) yönetimi")
public class FeatureController {

    private final FeatureService featureService;

    @GetMapping("/layer/{layerId}")
    @PreAuthorize("hasAuthority('CBS_VIEW')")
    @Operation(summary = "Katmanın tüm özelliklerini listele",
               description = "Belirtilen katmana ait tüm coğrafi özellikleri GeoJSON formatında döner. CBS_VIEW yetkisi gerektirir.")
    public ResponseEntity<ApiResponse<GeoJsonFeatureCollection>> getByLayerId(
            @PathVariable("layerId") UUID layerId) throws Exception {
        return ResponseEntity.ok(
            ApiResponse.success(featureService.getByLayerId(layerId))
        );
    }

    @GetMapping("/layer/{layerId}/bbox")
    @PreAuthorize("hasAuthority('CBS_VIEW')")
    @Operation(summary = "Bounding box ile sorgula",
               description = "Verilen koordinat sınırları (bounding box) içindeki özellikleri GeoJSON formatında döner.")
    public ResponseEntity<ApiResponse<GeoJsonFeatureCollection>> getByBbox(
            @PathVariable("layerId") UUID layerId,
            @Parameter(description = "Minimum X (boylam)") @RequestParam("minX") double minX,
            @Parameter(description = "Minimum Y (enlem)")  @RequestParam("minY") double minY,
            @Parameter(description = "Maksimum X (boylam)") @RequestParam("maxX") double maxX,
            @Parameter(description = "Maksimum Y (enlem)")  @RequestParam("maxY") double maxY) throws Exception {
        return ResponseEntity.ok(
            ApiResponse.success(
                featureService.getByBbox(layerId, minX, minY, maxX, maxY)
            )
        );
    }

    @GetMapping("/layer/{layerId}/nearby")
    @PreAuthorize("hasAuthority('CBS_VIEW')")
    @Operation(summary = "Yakın özellikleri bul",
               description = "Verilen koordinata belirtilen metre mesafe içindeki özellikleri döner (varsayılan: 1000 metre).")
    public ResponseEntity<ApiResponse<GeoJsonFeatureCollection>> getNearby(
            @PathVariable("layerId") UUID layerId,
            @Parameter(description = "Enlem") @RequestParam("lat") double lat,
            @Parameter(description = "Boylam") @RequestParam("lon") double lon,
            @Parameter(description = "Yarıçap (metre)") @RequestParam(value = "distance", defaultValue = "1000")
                double distanceMeters) throws Exception {
        return ResponseEntity.ok(
            ApiResponse.success(
                featureService.getNearby(layerId, lat, lon, distanceMeters)
            )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CBS_VIEW')")
    @Operation(summary = "Özellik detayı", description = "ID ile tek bir coğrafi özelliğin detayını getirir.")
    public ResponseEntity<ApiResponse<FeatureResponse>> getById(
            @PathVariable("id") UUID id) throws Exception {
        return ResponseEntity.ok(
            ApiResponse.success(featureService.getById(id))
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CBS_EDIT')")
    @Operation(summary = "Özellik oluştur",
               description = "GeoJSON formatında yeni bir coğrafi özellik (nokta, çizgi veya alan) oluşturur. CBS_EDIT yetkisi gerektirir.")
    public ResponseEntity<ApiResponse<FeatureResponse>> create(
            @Valid @RequestBody FeatureRequest request) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(
                featureService.create(request), "Feature oluşturuldu"
            )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CBS_EDIT')")
    @Operation(summary = "Özellik güncelle", description = "Mevcut coğrafi özelliğin bilgilerini ve geometrisini günceller. CBS_EDIT yetkisi gerektirir.")
    public ResponseEntity<ApiResponse<FeatureResponse>> update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody FeatureRequest request) throws Exception {
        return ResponseEntity.ok(
            ApiResponse.success(
                featureService.update(id, request), "Feature güncellendi"
            )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CBS_EDIT')")
    @Operation(summary = "Özellik sil", description = "Coğrafi özelliği soft-delete ile siler. CBS_EDIT yetkisi gerektirir.")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable("id") UUID id) throws Exception {
        featureService.delete(id);
        return ResponseEntity.ok(
            ApiResponse.success(null, "Feature silindi")
        );
    }
}
