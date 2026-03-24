package com.platform.dashboard.report.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.platform.dashboard.report.entity.Report;
import com.platform.dashboard.report.entity.ReportType;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {

    List<Report> findByCompanyIdAndActiveOrderByNameAsc(
        String companyId, Boolean active
    );

    List<Report> findByCompanyIdAndReportTypeAndActive(
        String companyId, ReportType reportType, Boolean active
    );
}