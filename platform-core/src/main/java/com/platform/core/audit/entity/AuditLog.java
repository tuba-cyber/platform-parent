package com.platform.core.audit.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "audit_logs")
@EntityListeners(AuditingEntityListener.class)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Kim yaptı
    private String username;
    private String companyId;

    // Ne yaptı
    @Column(nullable = false)
    private String action;

    // Hangi entity üzerinde
    private String entityType;
    private String entityId;

    // İstek detayları
    private String httpMethod;
    private String requestUrl;
    private String clientIp;

    // Sonuç
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditStatus status;

    private String errorMessage;

    // Ne kadar sürdü (ms)
    private Long durationMs;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}