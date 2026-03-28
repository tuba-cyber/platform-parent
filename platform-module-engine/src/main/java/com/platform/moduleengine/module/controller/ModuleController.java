package com.platform.moduleengine.module.controller;

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

import com.platform.core.audit.annotation.Auditable;
import com.platform.core.common.response.ApiResponse;
import com.platform.moduleengine.module.dto.ModuleRequest;
import com.platform.moduleengine.module.dto.ModuleResponse;
import com.platform.moduleengine.module.service.ModuleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ModuleResponse>>> getAll() {
        return ResponseEntity.ok(
            ApiResponse.success(moduleService.getAll())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ModuleResponse>> getById(
            @PathVariable("id") UUID id) {
        return ResponseEntity.ok(
            ApiResponse.success(moduleService.getById(id))
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MODULE_EDIT')")
    @Auditable(action = "MODULE_CREATE", entityType = "Module") 
    public ResponseEntity<ApiResponse<ModuleResponse>> create(
            @Valid @RequestBody ModuleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(moduleService.create(request), "Modül oluşturuldu")
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MODULE_EDIT')")
    public ResponseEntity<ApiResponse<ModuleResponse>> update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody ModuleRequest request) {
        return ResponseEntity.ok(
            ApiResponse.success(moduleService.update(id, request), "Modül güncellendi")
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MODULE_EDIT')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") UUID id) {
        moduleService.delete(id);
        return ResponseEntity.ok(
            ApiResponse.success(null, "Modül silindi")
        );
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAuthority('MODULE_EDIT')")
    public ResponseEntity<ApiResponse<ModuleResponse>> toggleActive(
            @PathVariable("id") UUID id) {
        return ResponseEntity.ok(
            ApiResponse.success(
                moduleService.toggleActive(id), "Modül durumu güncellendi"
            )
        );
    }
}