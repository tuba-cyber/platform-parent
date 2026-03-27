package com.platform.dashboard.dashboard.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.core.common.exception.ResourceNotFoundException;
import com.platform.core.security.model.UserPrincipal;
import com.platform.dashboard.dashboard.dto.DashboardRequest;
import com.platform.dashboard.dashboard.dto.DashboardResponse;
import com.platform.dashboard.dashboard.dto.DashboardWidgetRequest;
import com.platform.dashboard.dashboard.dto.DashboardWidgetResponse;
import com.platform.dashboard.dashboard.entity.Dashboard;
import com.platform.dashboard.dashboard.entity.DashboardWidget;
import com.platform.dashboard.dashboard.repository.DashboardRepository;
import com.platform.dashboard.dashboard.repository.DashboardWidgetRepository;
import com.platform.dashboard.widget.entity.Widget;
import com.platform.dashboard.widget.repository.WidgetRepository;
import com.platform.dashboard.widget.service.WidgetService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final DashboardWidgetRepository dashboardWidgetRepository;
    private final WidgetRepository widgetRepository;
    private final WidgetService widgetService;

    @Transactional
    public DashboardResponse create(DashboardRequest request) {
        String companyId = getCurrentCompanyId();

        // Eğer default işaretlendiyse diğerlerini kaldır
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            dashboardRepository
                .findByCompanyIdAndIsDefaultTrue(companyId)
                .ifPresent(d -> {
                    d.setIsDefault(false);
                    dashboardRepository.save(d);
                });
        }

        Dashboard dashboard = new Dashboard();
        dashboard.setName(request.getName());
        dashboard.setDescription(request.getDescription());
        dashboard.setIcon(request.getIcon());
        dashboard.setOrderIndex(request.getOrderIndex());
        dashboard.setIsDefault(request.getIsDefault());
        dashboard.setRequiredPermission(request.getRequiredPermission());
        dashboard.setCompanyId(companyId);

        dashboardRepository.save(dashboard);
        log.info("Dashboard oluşturuldu: {}", dashboard.getName());

        return toResponse(dashboard, false);
    }

    @Transactional(readOnly = true)
    public List<DashboardResponse> getAll() {
        String companyId = getCurrentCompanyId();
        return dashboardRepository
            .findByCompanyIdAndActiveOrderByOrderIndexAsc(companyId, true)
            .stream()
            .map(d -> toResponse(d, false))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DashboardResponse getById(UUID id) {
        Dashboard dashboard = dashboardRepository
            .findByIdWithWidgets(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Dashboard", id));
        return toResponse(dashboard, true);
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDefault() {
        String companyId = getCurrentCompanyId();
        Dashboard dashboard = dashboardRepository
            .findByCompanyIdAndIsDefaultTrue(companyId)
            .orElseThrow(() ->
                new ResourceNotFoundException("Varsayılan dashboard bulunamadı"));
        return toResponse(dashboard, true);
    }

    @Transactional
    public DashboardResponse update(UUID id, DashboardRequest request) {
        Dashboard dashboard = dashboardRepository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Dashboard", id));

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            dashboardRepository
                .findByCompanyIdAndIsDefaultTrue(dashboard.getCompanyId())
                .ifPresent(d -> {
                    if (!d.getId().equals(id)) {
                        d.setIsDefault(false);
                        dashboardRepository.save(d);
                    }
                });
        }

        dashboard.setName(request.getName());
        dashboard.setDescription(request.getDescription());
        dashboard.setIcon(request.getIcon());
        dashboard.setOrderIndex(request.getOrderIndex());
        dashboard.setIsDefault(request.getIsDefault());
        dashboard.setRequiredPermission(request.getRequiredPermission());

        dashboardRepository.save(dashboard);
        log.info("Dashboard güncellendi: {}", dashboard.getName());

        return toResponse(dashboard, false);
    }

    @Transactional
    public DashboardResponse addWidget(UUID dashboardId,
            DashboardWidgetRequest request) {
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
            .orElseThrow(() ->
                new ResourceNotFoundException("Dashboard", dashboardId));

        Widget widget = widgetRepository.findById(request.getWidgetId())
            .orElseThrow(() ->
                new ResourceNotFoundException("Widget", request.getWidgetId()));

        DashboardWidget dw = new DashboardWidget();
        dw.setDashboard(dashboard);
        dw.setWidget(widget);
        dw.setRowIndex(request.getRowIndex());
        dw.setColIndex(request.getColIndex());
        dw.setColSpan(request.getColSpan());
        dw.setRowSpan(request.getRowSpan());

        dashboardWidgetRepository.save(dw);
        log.info("Widget dashboard'a eklendi: {} → {}",
            widget.getName(), dashboard.getName());

        return getById(dashboardId);
    }

    @Transactional
    public void removeWidget(UUID dashboardId, UUID widgetId) {
        dashboardWidgetRepository
            .deleteByDashboardIdAndWidgetId(dashboardId, widgetId);
        log.info("Widget dashboard'dan kaldırıldı");
    }

    @Transactional
    public void delete(UUID id) {
        Dashboard dashboard = dashboardRepository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Dashboard", id));
        dashboard.setActive(false);
        dashboardRepository.save(dashboard);
        log.info("Dashboard pasif yapıldı: {}", dashboard.getName());
    }

    private String getCurrentCompanyId() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder
            .getContext().getAuthentication().getPrincipal();
        return principal.getCompanyId();
    }

    public DashboardResponse toResponse(Dashboard dashboard,
            boolean includeWidgets) {
        return DashboardResponse.builder()
                .id(dashboard.getId())
                .name(dashboard.getName())
                .description(dashboard.getDescription())
                .icon(dashboard.getIcon())
                .orderIndex(dashboard.getOrderIndex())
                .isDefault(dashboard.getIsDefault())
                .active(dashboard.getActive())
                .requiredPermission(dashboard.getRequiredPermission())
                .companyId(dashboard.getCompanyId())
                .widgets(includeWidgets && dashboard.getWidgets() != null
                    ? dashboard.getWidgets().stream()
                        .map(dw -> DashboardWidgetResponse.builder()
                            .id(dw.getId())
                            .widgetId(dw.getWidget().getId())
                            .widget(widgetService.toResponse(dw.getWidget()))
                            .rowIndex(dw.getRowIndex())
                            .colIndex(dw.getColIndex())
                            .colSpan(dw.getColSpan())
                            .rowSpan(dw.getRowSpan())
                            .build())
                        .collect(Collectors.toList())
                    : null)
                .createdAt(dashboard.getCreatedAt())
                .updatedAt(dashboard.getUpdatedAt())
                .build();
    }
}