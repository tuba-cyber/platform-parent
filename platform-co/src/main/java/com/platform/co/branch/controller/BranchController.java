package com.platform.co.branch.controller;

import com.platform.co.branch.dto.BranchRequest;
import com.platform.co.branch.dto.BranchResponse;
import com.platform.co.branch.service.BranchService;
import com.platform.core.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
@Tag(name = "Şube Yönetimi", description = "Şirketlere ait şube ve lokasyon yönetimi")
public class BranchController {

    private final BranchService branchService;

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Şirkete ait şubeleri listele",
               description = "Belirtilen şirketin tüm aktif şubelerini listeler.")
    public ResponseEntity<ApiResponse<List<BranchResponse>>> getByCompanyId(
            @PathVariable UUID companyId) {
        return ResponseEntity.ok(ApiResponse.success(branchService.getByCompanyId(companyId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Şube detayı", description = "ID ile şube bilgilerini getirir.")
    public ResponseEntity<ApiResponse<BranchResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(branchService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER_EDIT')")
    @Operation(summary = "Şube ekle",
               description = "Bir şirkete yeni şube ekler. 'headquarters: true' ise merkez olarak işaretlenir.")
    public ResponseEntity<ApiResponse<BranchResponse>> create(
            @Valid @RequestBody BranchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(branchService.create(request), "Şube oluşturuldu"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_EDIT')")
    @Operation(summary = "Şube güncelle", description = "Mevcut şube bilgilerini günceller.")
    public ResponseEntity<ApiResponse<BranchResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody BranchRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(branchService.update(id, request), "Şube güncellendi"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_EDIT')")
    @Operation(summary = "Şube sil", description = "Şubeyi pasif hale getirir (soft-delete).")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        branchService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Şube silindi"));
    }
}
