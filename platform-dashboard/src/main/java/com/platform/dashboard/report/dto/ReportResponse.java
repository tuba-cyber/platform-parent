package com.platform.dashboard.report.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.platform.dashboard.report.entity.ReportType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportResponse {

    private UUID id;
    private String name;
    private String description;
    private ReportType reportType;
    private String query;
    private String columnDefinitions;
    private String filterDefinitions;
    private String requiredPermission;
    private Boolean active;
    private String companyId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}