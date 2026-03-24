package com.platform.dashboard.dashboard.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.platform.dashboard.dashboard.entity.Dashboard;

@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, UUID> {

    List<Dashboard> findByCompanyIdAndActiveOrderByOrderIndexAsc(
        String companyId, Boolean active
    );

    Optional<Dashboard> findByCompanyIdAndIsDefaultTrue(String companyId);

    @Query("""
        SELECT d FROM Dashboard d
        LEFT JOIN FETCH d.widgets dw
        LEFT JOIN FETCH dw.widget
        WHERE d.id = :id
    """)
    Optional<Dashboard> findByIdWithWidgets(@Param("id") UUID id);
}