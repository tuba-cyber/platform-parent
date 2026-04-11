package com.platform.hr.position.controller;

import com.platform.core.common.response.ApiResponse;
import com.platform.hr.position.dto.PositionRequest;
import com.platform.hr.position.dto.PositionResponse;
import com.platform.hr.position.service.PositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
@Tag(name = "Pozisyon Yönetimi", description = "Şirket pozisyon ve unvan yönetimi işlemleri")
public class PositionController {

    private final PositionService positionService;

    @PostMapping
    @Operation(summary = "Pozisyon oluştur", description = "Yeni bir pozisyon/unvan kaydı oluşturur")
    public ResponseEntity<ApiResponse<PositionResponse>> create(@Valid @RequestBody PositionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(positionService.create(request));
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Şirkete göre pozisyonları listele")
    public ResponseEntity<ApiResponse<List<PositionResponse>>> getByCompany(
            @Parameter(description = "Şirket ID") @PathVariable UUID companyId) {
        return ResponseEntity.ok(positionService.getByCompany(companyId));
    }

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Departmana göre pozisyonları listele")
    public ResponseEntity<ApiResponse<List<PositionResponse>>> getByDepartment(
            @Parameter(description = "Departman ID") @PathVariable UUID departmentId) {
        return ResponseEntity.ok(positionService.getByDepartment(departmentId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Pozisyon detayı")
    public ResponseEntity<ApiResponse<PositionResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(positionService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Pozisyon güncelle")
    public ResponseEntity<ApiResponse<PositionResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody PositionRequest request) {
        return ResponseEntity.ok(positionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Pozisyonu pasife al")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(positionService.delete(id));
    }
}
