package com.platform.core.storage.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.platform.core.storage.entity.FileMetadata;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, UUID> {

    List<FileMetadata> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
        String entityType, String entityId
    );

    List<FileMetadata> findByCompanyIdAndActiveOrderByCreatedAtDesc(
        String companyId, Boolean active
    );

    List<FileMetadata> findByUploadedByAndActiveOrderByCreatedAtDesc(
        String uploadedBy, Boolean active
    );
}