package com.platform.core.audit.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.platform.core.audit.entity.AuditLog;
import com.platform.core.audit.entity.AuditStatus;
import com.platform.core.audit.repository.AuditLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Async
    public void log(String username,
                    String companyId,
                    String action,
                    String entityType,
                    String entityId,
                    String httpMethod,
                    String requestUrl,
                    String clientIp,
                    AuditStatus status,
                    String errorMessage,
                    Long durationMs) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setUsername(username);
            auditLog.setCompanyId(companyId);
            auditLog.setAction(action);
            auditLog.setEntityType(entityType);
            auditLog.setEntityId(entityId);
            auditLog.setHttpMethod(httpMethod);
            auditLog.setRequestUrl(requestUrl);
            auditLog.setClientIp(clientIp);
            auditLog.setStatus(status);
            auditLog.setErrorMessage(errorMessage);
            auditLog.setDurationMs(durationMs);

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Audit log kaydedilemedi: {}", e.getMessage());
        }
    }
}