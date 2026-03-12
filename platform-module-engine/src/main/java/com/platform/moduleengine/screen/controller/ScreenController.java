package com.platform.moduleengine.screen.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.core.common.response.ApiResponse;
import com.platform.moduleengine.screen.dto.ScreenRequest;
import com.platform.moduleengine.screen.dto.ScreenResponse;
import com.platform.moduleengine.screen.service.ScreenService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ScreenController {

    private final ScreenService screenService;

    @GetMapping("/sections/{sectionId}/screens")
    public ResponseEntity<ApiResponse<List<ScreenResponse>>> getBySectionId(
            @PathVariable("sectionId") UUID sectionId) {
        return ResponseEntity.ok(
            ApiResponse.success(screenService.getBySectionId(sectionId))
        );
    }

    @GetMapping("/screens/{id}")
    public ResponseEntity<ApiResponse<ScreenResponse>> getById(
            @PathVariable("id") UUID id) {
        return ResponseEntity.ok(
            ApiResponse.success(screenService.getById(id))
        );
    }

    @GetMapping("/screens/code/{code}")
    public ResponseEntity<ApiResponse<ScreenResponse>> getByCode(
            @PathVariable("code") String code) {
        return ResponseEntity.ok(
            ApiResponse.success(screenService.getByCode(code))
        );
    }

    @PostMapping("/sections/{sectionId}/screens")
    @PreAuthorize("hasAuthority('MODULE_EDIT')")
    public ResponseEntity<ApiResponse<ScreenResponse>> create(
            @PathVariable("sectionId") UUID sectionId,
            @Valid @RequestBody ScreenRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(
                screenService.create(sectionId, request), "Ekran oluşturuldu"
            )
        );
    }

    @PutMapping("/screens/{id}")
    @PreAuthorize("hasAuthority('MODULE_EDIT')")
    public ResponseEntity<ApiResponse<ScreenResponse>> update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody ScreenRequest request) {
        return ResponseEntity.ok(
            ApiResponse.success(
                screenService.update(id, request), "Ekran güncellendi"
            )
        );
    }

    @PatchMapping("/screens/{id}/template")
    @PreAuthorize("hasAuthority('MODULE_EDIT')")
    public ResponseEntity<ApiResponse<ScreenResponse>> updateTemplate(
            @PathVariable("id") UUID id,
            @RequestBody String template) {
        return ResponseEntity.ok(
            ApiResponse.success(
                screenService.updateTemplate(id, template), "Şablon güncellendi"
            )
        );
    }

    @DeleteMapping("/screens/{id}")
    @PreAuthorize("hasAuthority('MODULE_EDIT')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") UUID id) {
        screenService.delete(id);
        return ResponseEntity.ok(
            ApiResponse.success(null, "Ekran silindi")
        );
    }
}