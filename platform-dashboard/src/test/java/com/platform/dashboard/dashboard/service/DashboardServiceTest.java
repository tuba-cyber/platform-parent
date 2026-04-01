package com.platform.dashboard.dashboard.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.platform.core.common.exception.ResourceNotFoundException;
import com.platform.core.security.model.UserPrincipal;
import com.platform.dashboard.dashboard.dto.DashboardRequest;
import com.platform.dashboard.dashboard.dto.DashboardResponse;
import com.platform.dashboard.dashboard.dto.DashboardWidgetRequest;
import com.platform.dashboard.dashboard.entity.Dashboard;
import com.platform.dashboard.dashboard.entity.DashboardWidget;
import com.platform.dashboard.dashboard.repository.DashboardRepository;
import com.platform.dashboard.dashboard.repository.DashboardWidgetRepository;
import com.platform.dashboard.widget.entity.Widget;
import com.platform.dashboard.widget.entity.WidgetType;
import com.platform.dashboard.widget.entity.DataSourceType;
import com.platform.dashboard.widget.repository.WidgetRepository;
import com.platform.dashboard.widget.service.WidgetService;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock private DashboardRepository dashboardRepository;
    @Mock private DashboardWidgetRepository dashboardWidgetRepository;
    @Mock private WidgetRepository widgetRepository;
    @Mock private WidgetService widgetService;

    @InjectMocks
    private DashboardService dashboardService;

    private UserPrincipal testPrincipal;
    private Dashboard testDashboard;

    @BeforeEach
    void setUp() {
        testPrincipal = UserPrincipal.builder()
                .id(UUID.randomUUID())
                .username("admin")
                .password("")
                .companyId("company1")
                .roles(List.of("ADMIN"))
                .permissions(List.of("DASHBOARD_MANAGE"))
                .active(true)
                .build();

        testDashboard = new Dashboard();
        testDashboard.setId(UUID.randomUUID());
        testDashboard.setName("Ana Dashboard");
        testDashboard.setDescription("Ana sayfa dashboard'u");
        testDashboard.setIcon("dashboard");
        testDashboard.setOrderIndex(1);
        testDashboard.setIsDefault(true);
        testDashboard.setCompanyId("company1");
        testDashboard.setActive(true);
    }

    private void setSecurityContext() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(testPrincipal, null, testPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("Dashboard başarıyla oluşturulmalı")
    void shouldCreateDashboard() {
        setSecurityContext();

        DashboardRequest request = new DashboardRequest();
        request.setName("Yeni Dashboard");
        request.setDescription("Test dashboard");
        request.setOrderIndex(1);
        request.setIsDefault(false);

        when(dashboardRepository.save(any(Dashboard.class))).thenAnswer(inv -> {
            Dashboard d = inv.getArgument(0);
            d.setId(UUID.randomUUID());
            return d;
        });

        DashboardResponse response = dashboardService.create(request);

        assertThat(response.getName()).isEqualTo("Yeni Dashboard");
        verify(dashboardRepository).save(any(Dashboard.class));
    }

    @Test
    @DisplayName("Default dashboard oluşturulduğunda diğeri kaldırılmalı")
    void shouldRemoveOtherDefaultWhenCreatingDefault() {
        setSecurityContext();

        Dashboard existingDefault = new Dashboard();
        existingDefault.setId(UUID.randomUUID());
        existingDefault.setIsDefault(true);
        existingDefault.setCompanyId("company1");

        DashboardRequest request = new DashboardRequest();
        request.setName("Yeni Default");
        request.setIsDefault(true);
        request.setOrderIndex(1);

        when(dashboardRepository.findByCompanyIdAndIsDefaultTrue("company1"))
                .thenReturn(Optional.of(existingDefault));
        when(dashboardRepository.save(any(Dashboard.class))).thenAnswer(inv -> {
            Dashboard d = inv.getArgument(0);
            if (d.getId() == null) d.setId(UUID.randomUUID());
            return d;
        });

        dashboardService.create(request);

        assertThat(existingDefault.getIsDefault()).isFalse();
        verify(dashboardRepository, atLeast(2)).save(any(Dashboard.class));
    }

    @Test
    @DisplayName("Tüm dashboardlar listelenebilmeli")
    void shouldGetAllDashboards() {
        setSecurityContext();

        when(dashboardRepository.findByCompanyIdAndActiveOrderByOrderIndexAsc("company1", true))
                .thenReturn(List.of(testDashboard));

        List<DashboardResponse> result = dashboardService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Ana Dashboard");
    }

    @Test
    @DisplayName("Dashboard ID ile getirilmeli")
    void shouldGetDashboardById() {
        UUID dashboardId = testDashboard.getId();
        when(dashboardRepository.findByIdWithWidgets(dashboardId))
                .thenReturn(Optional.of(testDashboard));

        DashboardResponse response = dashboardService.getById(dashboardId);

        assertThat(response.getName()).isEqualTo("Ana Dashboard");
    }

    @Test
    @DisplayName("Default dashboard getirilmeli")
    void shouldGetDefaultDashboard() {
        setSecurityContext();

        when(dashboardRepository.findByCompanyIdAndIsDefaultTrue("company1"))
                .thenReturn(Optional.of(testDashboard));

        DashboardResponse response = dashboardService.getDefault();

        assertThat(response.getName()).isEqualTo("Ana Dashboard");
        assertThat(response.getIsDefault()).isTrue();
    }

    @Test
    @DisplayName("Dashboard bulunamazsa hata fırlatmalı")
    void shouldThrowWhenDashboardNotFound() {
        UUID id = UUID.randomUUID();
        when(dashboardRepository.findByIdWithWidgets(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dashboardService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Dashboard'a widget eklenebilmeli")
    void shouldAddWidgetToDashboard() {
        UUID dashboardId = testDashboard.getId();
        UUID widgetId = UUID.randomUUID();

        Widget widget = new Widget();
        widget.setId(widgetId);
        widget.setName("Kullanıcı Sayısı");
        widget.setWidgetType(WidgetType.COUNTER);

        DashboardWidgetRequest request = new DashboardWidgetRequest();
        request.setWidgetId(widgetId);
        request.setRowIndex(0);
        request.setColIndex(0);
        request.setColSpan(3);
        request.setRowSpan(1);

        when(dashboardRepository.findById(dashboardId)).thenReturn(Optional.of(testDashboard));
        when(widgetRepository.findById(widgetId)).thenReturn(Optional.of(widget));
        when(dashboardWidgetRepository.save(any(DashboardWidget.class))).thenReturn(new DashboardWidget());
        when(dashboardRepository.findByIdWithWidgets(dashboardId)).thenReturn(Optional.of(testDashboard));

        DashboardResponse response = dashboardService.addWidget(dashboardId, request);

        assertThat(response).isNotNull();
        verify(dashboardWidgetRepository).save(any(DashboardWidget.class));
    }

    @Test
    @DisplayName("Dashboard soft delete yapılmalı")
    void shouldSoftDeleteDashboard() {
        UUID dashboardId = testDashboard.getId();
        when(dashboardRepository.findById(dashboardId)).thenReturn(Optional.of(testDashboard));
        when(dashboardRepository.save(any(Dashboard.class))).thenReturn(testDashboard);

        dashboardService.delete(dashboardId);

        assertThat(testDashboard.getActive()).isFalse();
        verify(dashboardRepository).save(testDashboard);
    }

    @Test
    @DisplayName("Widget dashboard'dan kaldırılabilmeli")
    void shouldRemoveWidgetFromDashboard() {
        UUID dashboardId = UUID.randomUUID();
        UUID widgetId = UUID.randomUUID();

        dashboardService.removeWidget(dashboardId, widgetId);

        verify(dashboardWidgetRepository).deleteByDashboardIdAndWidgetId(dashboardId, widgetId);
    }
}
