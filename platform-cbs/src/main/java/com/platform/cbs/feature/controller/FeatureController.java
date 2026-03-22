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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/features")
@RequiredArgsConstructor
public class FeatureController {

    private final FeatureService featureService;

    @GetMapping("/layer/{layerId}")
    @PreAuthorize("hasAuthority('CBS_VIEW')")
    public ResponseEntity<ApiResponse<GeoJsonFeatureCollection>> getByLayerId(
            @PathVariable("layerId") UUID layerId) throws Exception {
        return ResponseEntity.ok(
            ApiResponse.success(featureService.getByLayerId(layerId))
        );
    }

    @GetMapping("/layer/{layerId}/bbox")
    @PreAuthorize("hasAuthority('CBS_VIEW')")
    public ResponseEntity<ApiResponse<GeoJsonFeatureCollection>> getByBbox(
            @PathVariable("layerId") UUID layerId,
            @RequestParam("minX") double minX,
            @RequestParam("minY") double minY,
            @RequestParam("maxX") double maxX,
            @RequestParam("maxY") double maxY) throws Exception {
        return ResponseEntity.ok(
            ApiResponse.success(
                featureService.getByBbox(layerId, minX, minY, maxX, maxY)
            )
        );
    }

    @GetMapping("/layer/{layerId}/nearby")
    @PreAuthorize("hasAuthority('CBS_VIEW')")
    public ResponseEntity<ApiResponse<GeoJsonFeatureCollection>> getNearby(
            @PathVariable("layerId") UUID layerId,
            @RequestParam("lat") double lat,
            @RequestParam("lon") double lon,
            @RequestParam(value = "distance", defaultValue = "1000")
                double distanceMeters) throws Exception {
        return ResponseEntity.ok(
            ApiResponse.success(
                featureService.getNearby(layerId, lat, lon, distanceMeters)
            )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CBS_VIEW')")
    public ResponseEntity<ApiResponse<FeatureResponse>> getById(
            @PathVariable("id") UUID id) throws Exception {
        return ResponseEntity.ok(
            ApiResponse.success(featureService.getById(id))
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CBS_EDIT')")
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
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable("id") UUID id) throws Exception {
        featureService.delete(id);
        return ResponseEntity.ok(
            ApiResponse.success(null, "Feature silindi")
        );
    }
}