package com.platform.dashboard.widget.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.platform.dashboard.widget.entity.Widget;
import com.platform.dashboard.widget.entity.WidgetType;

@Repository
public interface WidgetRepository extends JpaRepository<Widget, UUID> {

    List<Widget> findByCompanyIdAndActiveOrderByNameAsc(
        String companyId, Boolean active
    );

    List<Widget> findByCompanyIdAndWidgetTypeAndActive(
        String companyId, WidgetType widgetType, Boolean active
    );
}