package com.platform.dashboard.report.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.core.common.exception.ResourceNotFoundException;
import com.platform.core.security.model.UserPrincipal;
import com.platform.dashboard.report.dto.ReportRequest;
import com.platform.dashboard.report.dto.ReportResponse;
import com.platform.dashboard.report.entity.Report;
import com.platform.dashboard.report.repository.ReportRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    @Transactional
    public ReportResponse create(ReportRequest request) {
        String companyId = getCurrentCompanyId();

        Report report = new Report();
        report.setName(request.getName());
        report.setDescription(request.getDescription());
        report.setReportType(request.getReportType());
        report.setQuery(request.getQuery());
        report.setColumnDefinitions(request.getColumnDefinitions());
        report.setFilterDefinitions(request.getFilterDefinitions());
        report.setRequiredPermission(request.getRequiredPermission());
        report.setCompanyId(companyId);

        reportRepository.save(report);
        log.info("Rapor oluşturuldu: {}", report.getName());

        return toResponse(report);
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getAll() {
        String companyId = getCurrentCompanyId();
        return reportRepository
            .findByCompanyIdAndActiveOrderByNameAsc(companyId, true)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReportResponse getById(UUID id) {
        Report report = reportRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Rapor", id));
        return toResponse(report);
    }

    @Transactional
    public ReportResponse update(UUID id, ReportRequest request) {
        Report report = reportRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Rapor", id));

        report.setName(request.getName());
        report.setDescription(request.getDescription());
        report.setReportType(request.getReportType());
        report.setQuery(request.getQuery());
        report.setColumnDefinitions(request.getColumnDefinitions());
        report.setFilterDefinitions(request.getFilterDefinitions());
        report.setRequiredPermission(request.getRequiredPermission());

        reportRepository.save(report);
        log.info("Rapor güncellendi: {}", report.getName());

        return toResponse(report);
    }

    @Transactional
    public void delete(UUID id) {
        Report report = reportRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Rapor", id));
        report.setActive(false);
        reportRepository.save(report);
        log.info("Rapor pasif yapıldı: {}", report.getName());
    }

    private String getCurrentCompanyId() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder
            .getContext().getAuthentication().getPrincipal();
        return principal.getCompanyId();
    }

    public ReportResponse toResponse(Report report) {
        return ReportResponse.builder()
                .id(report.getId())
                .name(report.getName())
                .description(report.getDescription())
                .reportType(report.getReportType())
                .query(report.getQuery())
                .columnDefinitions(report.getColumnDefinitions())
                .filterDefinitions(report.getFilterDefinitions())
                .requiredPermission(report.getRequiredPermission())
                .active(report.getActive())
                .companyId(report.getCompanyId())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
}