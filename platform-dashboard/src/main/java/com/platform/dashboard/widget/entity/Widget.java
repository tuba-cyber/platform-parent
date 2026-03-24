package com.platform.dashboard.widget.entity;

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
@Table(name = "widgets")
public class Widget extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WidgetType widgetType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DataSourceType dataSourceType;

    // Veri kaynağı — SQL sorgusu veya API URL
    @Column(columnDefinition = "TEXT")
    private String dataSource;

    // ECharts konfigürasyonu (JSON)
    @Column(columnDefinition = "TEXT")
    private String chartConfig;

    // Yenileme aralığı (saniye, 0 = manuel)
    @Column(nullable = false)
    private Integer refreshInterval = 0;

    private String requiredPermission;

    @Column(nullable = false)
    private String companyId;
}