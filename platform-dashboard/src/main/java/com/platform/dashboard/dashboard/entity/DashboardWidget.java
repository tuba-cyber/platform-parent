package com.platform.dashboard.dashboard.entity;

import com.platform.core.common.entity.BaseEntity;
import com.platform.dashboard.widget.entity.Widget;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "dashboard_widgets")
public class DashboardWidget extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dashboard_id", nullable = false)
    private Dashboard dashboard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "widget_id", nullable = false)
    private Widget widget;

    // Grid pozisyonu
    @Column(nullable = false)
    private Integer rowIndex = 0;

    @Column(nullable = false)
    private Integer colIndex = 0;

    // Grid boyutu (12 kolonlu grid)
    @Column(nullable = false)
    private Integer colSpan = 6;

    @Column(nullable = false)
    private Integer rowSpan = 4;
}