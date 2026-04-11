package com.platform.hr.department.controller;

import com.platform.core.common.response.ApiResponse;
import com.platform.hr.department.dto.DepartmentRequest;
import com.platform.hr.department.dto.DepartmentResponse;
import com.platform.hr.department.service.DepartmentService;
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
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Tag(name = "Departman Yönetimi", description = "Şirket departmanlarını oluşturma, listeleme ve güncelleme işlemleri")
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @Operation(summary = "Departman oluştur", description = "Yeni bir departman kaydı oluşturur")
    public ResponseEntity<ApiResponse<DepartmentResponse>> create(@Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.create(request));
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Şirkete göre departmanları listele")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getByCompany(
            @Parameter(description = "Şirket ID") @PathVariable UUID companyId) {
        return ResponseEntity.ok(departmentService.getByCompany(companyId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Departman detayı")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(departmentService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Departman güncelle")
    public ResponseEntity<ApiResponse<DepartmentResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.ok(departmentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Departmanı pasife al")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(departmentService.delete(id));
    }
}
