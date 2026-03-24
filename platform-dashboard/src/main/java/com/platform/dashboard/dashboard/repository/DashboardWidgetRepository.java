package com.platform.dashboard.dashboard.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.platform.dashboard.dashboard.entity.DashboardWidget;

@Repository
public interface DashboardWidgetRepository
        extends JpaRepository<DashboardWidget, UUID> {

    List<DashboardWidget> findByDashboardIdOrderByRowIndexAscColIndexAsc(
        UUID dashboardId
    );

    void deleteByDashboardIdAndWidgetId(UUID dashboardId, UUID widgetId);
}