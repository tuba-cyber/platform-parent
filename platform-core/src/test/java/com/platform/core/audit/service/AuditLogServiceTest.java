package com.platform.core.audit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.platform.core.audit.entity.AuditLog;
import com.platform.core.audit.entity.AuditStatus;
import com.platform.core.audit.repository.AuditLogRepository;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    @Test
    @DisplayName("Audit log başarıyla kaydedilmeli")
    void shouldSaveAuditLog() {
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        auditLogService.log(
                "testuser", "company1", "CREATE",
                "Module", "module-123", "POST",
                "/api/v1/modules", "192.168.1.1",
                AuditStatus.SUCCESS, null, 150L
        );

        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    @DisplayName("Audit log kayıt hatası yakalanmalı")
    void shouldHandleSaveException() {
        when(auditLogRepository.save(any(AuditLog.class)))
                .thenThrow(new RuntimeException("DB error"));

        // Should not throw exception
        auditLogService.log(
                "testuser", "company1", "CREATE",
                "Module", "module-123", "POST",
                "/api/v1/modules", "192.168.1.1",
                AuditStatus.SUCCESS, null, 150L
        );

        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    @DisplayName("Hata durumunda audit log kaydedilmeli")
    void shouldSaveFailureAuditLog() {
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        auditLogService.log(
                "testuser", "company1", "DELETE",
                "Module", "module-123", "DELETE",
                "/api/v1/modules/123", "192.168.1.1",
                AuditStatus.FAILURE, "Unauthorized access", 50L
        );

        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }
}
