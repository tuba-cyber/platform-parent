package com.platform.moduleengine.section.controller;

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
import com.platform.moduleengine.section.dto.SectionRequest;
import com.platform.moduleengine.section.dto.SectionResponse;
import com.platform.moduleengine.section.service.SectionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Bölüm Yönetimi", description = "Modül bölümlerinin (section) yönetimi. Her bölüm birden fazla ekran içerebilir.")
public class SectionController {

    private final SectionService sectionService;

    @GetMapping("/modules/{moduleId}/sections")
    @Operation(summary = "Modüle ait bölümleri listele", description = "Belirtilen modüle ait tüm bölümleri döner.")
    public ResponseEntity<ApiResponse<List<SectionResponse>>> getByModuleId(
            @PathVariable("moduleId") UUID moduleId) {
        return ResponseEntity.ok(
            ApiResponse.success(sectionService.getByModuleId(moduleId))
        );
    }

    @GetMapping("/sections/{id}")
    @Operation(summary = "Bölüm detayı", description = "ID ile tek bir bölümün detayını getirir.")
    public ResponseEntity<ApiResponse<SectionResponse>> getById(
            @PathVariable("id") UUID id) {
        return ResponseEntity.ok(
            ApiResponse.success(sectionService.getById(id))
        );
    }

    @PostMapping("/modules/{moduleId}/sections")
    @PreAuthorize("hasAuthority('MODULE_EDIT')")
    @Operation(summary = "Bölüm oluştur", description = "Belirtilen modüle yeni bir bölüm ekler. MODULE_EDIT yetkisi gerektirir.")
    public ResponseEntity<ApiResponse<SectionResponse>> create(
            @PathVariable("moduleId") UUID moduleId,
            @Valid @RequestBody SectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(
                sectionService.create(moduleId, request), "Bölüm oluşturuldu"
            )
        );
    }

    @PutMapping("/sections/{id}")
    @PreAuthorize("hasAuthority('MODULE_EDIT')")
    public ResponseEntity<ApiResponse<SectionResponse>> update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody SectionRequest request) {
        return ResponseEntity.ok(
            ApiResponse.success(
                sectionService.update(id, request), "Bölüm güncellendi"
            )
        );
    }

    @DeleteMapping("/sections/{id}")
    @PreAuthorize("hasAuthority('MODULE_EDIT')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") UUID id) {
        sectionService.delete(id);
        return ResponseEntity.ok(
            ApiResponse.success(null, "Bölüm silindi")
        );
    }
}