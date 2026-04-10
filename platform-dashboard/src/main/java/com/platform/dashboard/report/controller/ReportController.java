package com.platform.dashboard.report.controller;

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
import com.platform.dashboard.report.dto.ReportRequest;
import com.platform.dashboard.report.dto.ReportResponse;
import com.platform.dashboard.report.service.ReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Rapor Yönetimi", description = "Rapor tanımları yönetimi. Rapor türleri: TABLE, CHART, MAP, MIXED")
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @Operation(summary = "Tüm raporları listele", description = "Tenant'a ait tüm rapor tanımlarını listeler.")
    public ResponseEntity<ApiResponse<List<ReportResponse>>> getAll() {
        return ResponseEntity.ok(
            ApiResponse.success(reportService.getAll())
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @Operation(summary = "Rapor detayı", description = "ID ile tek bir rapor tanımının detayını getirir.")
    public ResponseEntity<ApiResponse<ReportResponse>> getById(
            @PathVariable("id") UUID id) {
        return ResponseEntity.ok(
            ApiResponse.success(reportService.getById(id))
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @Operation(summary = "Rapor oluştur",
               description = "Yeni bir rapor tanımı oluşturur. Rapor türüne göre veri kaynağı yapılandırılır.")
    public ResponseEntity<ApiResponse<ReportResponse>> create(
            @Valid @RequestBody ReportRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(
                reportService.create(request), "Rapor oluşturuldu"
            )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @Operation(summary = "Rapor güncelle", description = "Mevcut rapor tanımının bilgilerini günceller.")
    public ResponseEntity<ApiResponse<ReportResponse>> update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody ReportRequest request) {
        return ResponseEntity.ok(
            ApiResponse.success(
                reportService.update(id, request), "Rapor güncellendi"
            )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @Operation(summary = "Rapor sil", description = "Rapor tanımını soft-delete ile siler.")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable("id") UUID id) {
        reportService.delete(id);
        return ResponseEntity.ok(
            ApiResponse.success(null, "Rapor silindi")
        );
    }
}
