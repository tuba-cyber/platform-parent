package com.platform.core.storage.controller;

import java.util.UUID;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.platform.core.common.response.ApiResponse;
import com.platform.core.security.model.UserPrincipal;
import com.platform.core.storage.dto.FileUploadResponse;
import com.platform.core.storage.entity.FileMetadata;
import com.platform.core.storage.repository.FileMetadataRepository;
import com.platform.core.storage.service.StorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final StorageService storageService;
    private final FileMetadataRepository fileMetadataRepository;

    @PostMapping(value = "/upload"/*, 
    	    consumes = MediaType.MULTIPART_FORM_DATA_VALUE */) 
    public ResponseEntity<ApiResponse<FileUploadResponse>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder,
            @RequestParam(value = "entityType", required = false) String entityType,
            @RequestParam(value = "entityId", required = false) String entityId) {

        UserPrincipal principal = getCurrentUser();

        // Dosyayı kaydet
        String storedPath = storageService.store(
            file, principal.getCompanyId(), folder);

        // Metadata kaydet
        FileMetadata metadata = new FileMetadata();
        metadata.setOriginalFilename(file.getOriginalFilename());
        metadata.setStoredPath(storedPath);
        metadata.setContentType(file.getContentType());
        metadata.setFileSize(file.getSize());
        metadata.setFolder(folder);
        metadata.setUploadedBy(principal.getUsername());
        metadata.setCompanyId(principal.getCompanyId());
        metadata.setEntityType(entityType);
        metadata.setEntityId(entityId);

        fileMetadataRepository.save(metadata);

        FileUploadResponse response = FileUploadResponse.builder()
                .id(metadata.getId())
                .originalFilename(metadata.getOriginalFilename())
                .contentType(metadata.getContentType())
                .fileSize(metadata.getFileSize())
                .folder(metadata.getFolder())
                .fileUrl(storageService.getFileUrl(storedPath))
                .entityType(metadata.getEntityType())
                .entityId(metadata.getEntityId())
                .uploadedBy(metadata.getUploadedBy())
                .build();

        log.info("Dosya yüklendi: {}", file.getOriginalFilename());
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(response, "Dosya yüklendi")
        );
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> download(
            @PathVariable("id") UUID id) {

        FileMetadata metadata = fileMetadataRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Dosya bulunamadı"));

        InputStreamResource resource = new InputStreamResource(
            storageService.retrieve(metadata.getStoredPath())
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" +
                    metadata.getOriginalFilename() + "\"")
                .contentType(MediaType.parseMediaType(
                    metadata.getContentType()))
                .contentLength(metadata.getFileSize())
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable("id") UUID id) {

        FileMetadata metadata = fileMetadataRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Dosya bulunamadı"));

        storageService.delete(metadata.getStoredPath());
        metadata.setActive(false);
        fileMetadataRepository.save(metadata);

        return ResponseEntity.ok(
            ApiResponse.success(null, "Dosya silindi")
        );
    }

    private UserPrincipal getCurrentUser() {
        return (UserPrincipal) SecurityContextHolder
            .getContext().getAuthentication().getPrincipal();
    }
}