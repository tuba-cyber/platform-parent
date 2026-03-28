package com.platform.core.storage.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileUploadResponse {

    private UUID id;
    private String originalFilename;
    private String contentType;
    private Long fileSize;
    private String folder;
    private String fileUrl;
    private String entityType;
    private String entityId;
    private String uploadedBy;
}