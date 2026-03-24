package com.platform.dashboard.dashboard.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardResponse {

    private UUID id;
    private String name;
    private String description;
    private String icon;
    private Integer orderIndex;
    private Boolean isDefault;
    private Boolean active;
    private String requiredPermission;
    private String companyId;
    private List<DashboardWidgetResponse> widgets;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}