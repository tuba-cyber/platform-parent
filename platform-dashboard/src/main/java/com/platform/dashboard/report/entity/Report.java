package com.platform.dashboard.report.entity;

import com.platform.core.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "reports")
public class Report extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType reportType;

    // SQL sorgusu
    @Column(columnDefinition = "TEXT", nullable = false)
    private String query;

    // Kolon tanımları (JSON)
    @Column(columnDefinition = "TEXT")
    private String columnDefinitions;

    // Filtre tanımları (JSON)
    @Column(columnDefinition = "TEXT")
    private String filterDefinitions;

    private String requiredPermission;

    @Column(nullable = false)
    private String companyId;
}