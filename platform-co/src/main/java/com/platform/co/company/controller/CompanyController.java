package com.platform.co.company.controller;

import com.platform.co.company.dto.CompanyRequest;
import com.platform.co.company.dto.CompanyResponse;
import com.platform.co.company.service.CompanyService;
import com.platform.core.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
@Tag(name = "Şirket Yönetimi", description = "Platform'u kullanacak şirket ve kurumların yönetimi")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    @Operation(
        summary = "Tüm şirketleri listele",
        description = "Sistemdeki aktif tüm şirketleri listeler."
    )
    public ResponseEntity<ApiResponse<List<CompanyResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(companyService.getAll()));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Şirket detayı",
        description = "ID ile bir şirketin tüm bilgilerini getirir."
    )
    public ResponseEntity<ApiResponse<CompanyResponse>> getById(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(companyService.getById(id)));
    }

    @GetMapping("/code/{code}")
    @Operation(
        summary = "Kod ile şirket getir",
        description = "Benzersiz şirket kodu (ör: PLATFORM) ile şirketi getirir."
    )
    public ResponseEntity<ApiResponse<CompanyResponse>> getByCode(
            @PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.success(companyService.getByCode(code)));
    }

    @GetMapping("/search")
    @Operation(
        summary = "Şirket ara",
        description = "Şirket adı, şehir veya sektör alanlarında arama yapar."
    )
    public ResponseEntity<ApiResponse<List<CompanyResponse>>> search(
            @Parameter(description = "Arama kelimesi") @RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.success(companyService.search(keyword)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER_EDIT')")
    @Operation(
        summary = "Yeni şirket ekle",
        description = "Platforma yeni bir şirket/kurum kaydeder. USER_EDIT yetkisi gerektirir."
    )
    public ResponseEntity<ApiResponse<CompanyResponse>> create(
            @Valid @RequestBody CompanyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(companyService.create(request), "Şirket oluşturuldu"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_EDIT')")
    @Operation(
        summary = "Şirket güncelle",
        description = "Mevcut şirketin bilgilerini günceller. USER_EDIT yetkisi gerektirir."
    )
    public ResponseEntity<ApiResponse<CompanyResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody CompanyRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(companyService.update(id, request), "Şirket güncellendi"));
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAuthority('USER_EDIT')")
    @Operation(
        summary = "Şirket aktif/pasif yap",
        description = "Şirketin aktiflik durumunu değiştirir. Pasif şirketin kullanıcıları giriş yapamaz."
    )
    public ResponseEntity<ApiResponse<CompanyResponse>> toggleActive(
            @PathVariable UUID id) {
        return ResponseEntity.ok(
                ApiResponse.success(companyService.toggleActive(id), "Şirket durumu güncellendi"));
    }
}
