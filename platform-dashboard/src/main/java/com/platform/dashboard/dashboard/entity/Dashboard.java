package com.platform.dashboard.dashboard.entity;

import java.util.List;

import com.platform.core.common.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "dashboards")
public class Dashboard extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;
    private String icon;

    @Column(nullable = false)
    private Integer orderIndex = 0;

    @Column(nullable = false)
    private Boolean isDefault = false;

    private String requiredPermission;

    @Column(nullable = false)
    private String companyId;

    @OneToMany(mappedBy = "dashboard",
               cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    @OrderBy("rowIndex ASC, colIndex ASC")
    private List<DashboardWidget> widgets;
}