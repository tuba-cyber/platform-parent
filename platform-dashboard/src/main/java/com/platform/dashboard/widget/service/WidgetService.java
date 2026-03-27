package com.platform.dashboard.widget.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.core.common.exception.ResourceNotFoundException;
import com.platform.core.security.model.UserPrincipal;
import com.platform.dashboard.widget.dto.WidgetRequest;
import com.platform.dashboard.widget.dto.WidgetResponse;
import com.platform.dashboard.widget.entity.Widget;
import com.platform.dashboard.widget.repository.WidgetRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WidgetService {

    private final WidgetRepository widgetRepository;

    @Transactional
    public WidgetResponse create(WidgetRequest request) {
        String companyId = getCurrentCompanyId();

        Widget widget = new Widget();
        widget.setName(request.getName());
        widget.setDescription(request.getDescription());
        widget.setWidgetType(request.getWidgetType());
        widget.setDataSourceType(request.getDataSourceType());
        widget.setDataSource(request.getDataSource());
        widget.setChartConfig(request.getChartConfig());
        widget.setRefreshInterval(request.getRefreshInterval());
        widget.setRequiredPermission(request.getRequiredPermission());
        widget.setCompanyId(companyId);

        widgetRepository.save(widget);
        log.info("Widget oluşturuldu: {}", widget.getName());

        return toResponse(widget);
    }

    @Transactional(readOnly = true)
    public List<WidgetResponse> getAll() {
        String companyId = getCurrentCompanyId();
        return widgetRepository
            .findByCompanyIdAndActiveOrderByNameAsc(companyId, true)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WidgetResponse getById(UUID id) {
        Widget widget = widgetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Widget", id));
        return toResponse(widget);
    }

    @Transactional
    public WidgetResponse update(UUID id, WidgetRequest request) {
        Widget widget = widgetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Widget", id));

        widget.setName(request.getName());
        widget.setDescription(request.getDescription());
        widget.setWidgetType(request.getWidgetType());
        widget.setDataSourceType(request.getDataSourceType());
        widget.setDataSource(request.getDataSource());
        widget.setChartConfig(request.getChartConfig());
        widget.setRefreshInterval(request.getRefreshInterval());
        widget.setRequiredPermission(request.getRequiredPermission());

        widgetRepository.save(widget);
        log.info("Widget güncellendi: {}", widget.getName());

        return toResponse(widget);
    }

    @Transactional
    public void delete(UUID id) {
        Widget widget = widgetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Widget", id));
        widget.setActive(false);
        widgetRepository.save(widget);
        log.info("Widget pasif yapıldı: {}", widget.getName());
    }

    private String getCurrentCompanyId() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder
            .getContext().getAuthentication().getPrincipal();
        return principal.getCompanyId();
    }

    public WidgetResponse toResponse(Widget widget) {
        return WidgetResponse.builder()
                .id(widget.getId())
                .name(widget.getName())
                .description(widget.getDescription())
                .widgetType(widget.getWidgetType())
                .dataSourceType(widget.getDataSourceType())
                .dataSource(widget.getDataSource())
                .chartConfig(widget.getChartConfig())
                .refreshInterval(widget.getRefreshInterval())
                .requiredPermission(widget.getRequiredPermission())
                .active(widget.getActive())
                .companyId(widget.getCompanyId())
                .createdAt(widget.getCreatedAt())
                .updatedAt(widget.getUpdatedAt())
                .build();
    }
}