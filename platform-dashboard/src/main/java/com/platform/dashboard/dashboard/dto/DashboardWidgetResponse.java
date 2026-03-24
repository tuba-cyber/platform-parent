package com.platform.dashboard.dashboard.dto;

import java.util.UUID;

import com.platform.dashboard.widget.dto.WidgetResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardWidgetResponse {

    private UUID id;
    private UUID widgetId;
    private WidgetResponse widget;
    private Integer rowIndex;
    private Integer colIndex;
    private Integer colSpan;
    private Integer rowSpan;
}