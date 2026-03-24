package com.platform.dashboard.widget.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.platform.dashboard.widget.entity.DataSourceType;
import com.platform.dashboard.widget.entity.WidgetType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WidgetResponse {

    private UUID id;
    private String name;
    private String description;
    private WidgetType widgetType;
    private DataSourceType dataSourceType;
    private String dataSource;
    private String chartConfig;
    private Integer refreshInterval;
    private String requiredPermission;
    private Boolean active;
    private String companyId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}