package com.platform.core.audit.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.platform.core.audit.entity.AuditLog;
import com.platform.core.audit.entity.AuditStatus;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    List<AuditLog> findByUsernameOrderByCreatedAtDesc(String username);

    List<AuditLog> findByCompanyIdOrderByCreatedAtDesc(String companyId);

    List<AuditLog> findByCompanyIdAndStatusOrderByCreatedAtDesc(
        String companyId, AuditStatus status
    );

    List<AuditLog> findByCompanyIdAndCreatedAtBetweenOrderByCreatedAtDesc(
        String companyId, LocalDateTime start, LocalDateTime end
    );

    List<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
        String entityType, String entityId
    );
}