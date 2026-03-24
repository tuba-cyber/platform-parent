package com.platform.dashboard.dashboard.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardWidgetRequest {

    @NotNull(message = "Widget ID boş olamaz")
    private UUID widgetId;

    private Integer rowIndex = 0;
    private Integer colIndex = 0;
    private Integer colSpan = 6;
    private Integer rowSpan = 4;
}