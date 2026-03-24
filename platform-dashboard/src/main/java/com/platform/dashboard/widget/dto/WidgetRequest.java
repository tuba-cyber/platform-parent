package com.platform.dashboard.widget.dto;

import com.platform.dashboard.widget.entity.DataSourceType;
import com.platform.dashboard.widget.entity.WidgetType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WidgetRequest {

    @NotBlank(message = "Widget adı boş olamaz")
    private String name;

    private String description;

    @NotNull(message = "Widget tipi boş olamaz")
    private WidgetType widgetType;

    @NotNull(message = "Veri kaynağı tipi boş olamaz")
    private DataSourceType dataSourceType;

    private String dataSource;
    private String chartConfig;
    private Integer refreshInterval = 0;
    private String requiredPermission;
}