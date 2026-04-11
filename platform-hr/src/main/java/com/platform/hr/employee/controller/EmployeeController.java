package com.platform.hr.employee.controller;

import com.platform.core.common.response.ApiResponse;
import com.platform.hr.employee.dto.EmployeeRequest;
import com.platform.hr.employee.dto.EmployeeResponse;
import com.platform.hr.employee.entity.EmployeeStatus;
import com.platform.hr.employee.service.EmployeeService;
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
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(name = "Çalışan Yönetimi", description = "Çalışan profili oluşturma, güncelleme, listeleme ve durum yönetimi")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @Operation(summary = "Çalışan oluştur", description = "Yeni bir çalışan profili oluşturur")
    public ResponseEntity<ApiResponse<EmployeeResponse>> create(@Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(request));
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Şirkete göre çalışanları listele")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getByCompany(
            @Parameter(description = "Şirket ID") @PathVariable UUID companyId) {
        return ResponseEntity.ok(employeeService.getByCompany(companyId));
    }

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Departmana göre çalışanları listele")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getByDepartment(
            @Parameter(description = "Departman ID") @PathVariable UUID departmentId) {
        return ResponseEntity.ok(employeeService.getByDepartment(departmentId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Çalışan detayı")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeService.getById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Çalışan ara", description = "Ad, soyad veya sicil numarasına göre arama yapar")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> search(
            @Parameter(description = "Şirket ID") @RequestParam UUID companyId,
            @Parameter(description = "Arama terimi") @RequestParam String q) {
        return ResponseEntity.ok(employeeService.search(companyId, q));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Çalışan güncelle")
    public ResponseEntity<ApiResponse<EmployeeResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(employeeService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Çalışan durumunu güncelle", description = "AKTIF, PASIF, IZINDE, ISTIFA, ISTEN_CIKARILDI, EMEKLI")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateStatus(
            @PathVariable UUID id,
            @Parameter(description = "Yeni durum") @RequestParam EmployeeStatus status) {
        return ResponseEntity.ok(employeeService.updateStatus(id, status));
    }
}
