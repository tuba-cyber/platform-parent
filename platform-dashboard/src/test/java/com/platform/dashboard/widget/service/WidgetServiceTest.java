package com.platform.dashboard.widget.service;

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
import com.platform.dashboard.widget.dto.WidgetRequest;
import com.platform.dashboard.widget.dto.WidgetResponse;
import com.platform.dashboard.widget.entity.Widget;
import com.platform.dashboard.widget.entity.WidgetType;
import com.platform.dashboard.widget.entity.DataSourceType;
import com.platform.dashboard.widget.repository.WidgetRepository;

@ExtendWith(MockitoExtension.class)
class WidgetServiceTest {

    @Mock
    private WidgetRepository widgetRepository;

    @InjectMocks
    private WidgetService widgetService;

    private UserPrincipal testPrincipal;
    private Widget testWidget;

    @BeforeEach
    void setUp() {
        testPrincipal = UserPrincipal.builder()
                .id(UUID.randomUUID())
                .username("admin")
                .password("")
                .companyId("company1")
                .roles(List.of("ADMIN"))
                .permissions(List.of("WIDGET_MANAGE"))
                .active(true)
                .build();

        testWidget = new Widget();
        testWidget.setId(UUID.randomUUID());
        testWidget.setName("Kullanıcı Sayısı");
        testWidget.setDescription("Toplam kullanıcı sayısını gösteren widget");
        testWidget.setWidgetType(WidgetType.COUNTER);
        testWidget.setDataSourceType(DataSourceType.SQL);
        testWidget.setDataSource("SELECT COUNT(*) FROM users");
        testWidget.setRefreshInterval(300);
        testWidget.setCompanyId("company1");
        testWidget.setActive(true);
    }

    private void setSecurityContext() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(testPrincipal, null, testPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("Widget başarıyla oluşturulmalı")
    void shouldCreateWidget() {
        setSecurityContext();

        WidgetRequest request = new WidgetRequest();
        request.setName("Kullanıcı Sayısı");
        request.setDescription("Toplam kullanıcı sayısını gösteren widget");
        request.setWidgetType(WidgetType.COUNTER);
        request.setDataSourceType(DataSourceType.SQL);
        request.setDataSource("SELECT COUNT(*) FROM users");
        request.setRefreshInterval(300);

        when(widgetRepository.save(any(Widget.class))).thenAnswer(inv -> {
            Widget w = inv.getArgument(0);
            w.setId(UUID.randomUUID());
            return w;
        });

        WidgetResponse response = widgetService.create(request);

        assertThat(response.getName()).isEqualTo("Kullanıcı Sayısı");
        assertThat(response.getWidgetType()).isEqualTo(WidgetType.COUNTER);
        assertThat(response.getCompanyId()).isEqualTo("company1");
        verify(widgetRepository).save(any(Widget.class));
    }

    @Test
    @DisplayName("Tüm widgetler listelenebilmeli")
    void shouldGetAllWidgets() {
        setSecurityContext();

        when(widgetRepository.findByCompanyIdAndActiveOrderByNameAsc("company1", true))
                .thenReturn(List.of(testWidget));

        List<WidgetResponse> result = widgetService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Kullanıcı Sayısı");
        assertThat(result.get(0).getWidgetType()).isEqualTo(WidgetType.COUNTER);
    }

    @Test
    @DisplayName("Widget ID ile getirilmeli")
    void shouldGetWidgetById() {
        UUID widgetId = testWidget.getId();
        when(widgetRepository.findById(widgetId)).thenReturn(Optional.of(testWidget));

        WidgetResponse response = widgetService.getById(widgetId);

        assertThat(response.getName()).isEqualTo("Kullanıcı Sayısı");
        assertThat(response.getDataSourceType()).isEqualTo(DataSourceType.SQL);
    }

    @Test
    @DisplayName("Widget bulunamazsa hata fırlatmalı")
    void shouldThrowWhenWidgetNotFound() {
        UUID id = UUID.randomUUID();
        when(widgetRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> widgetService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Widget");
    }

    @Test
    @DisplayName("Widget başarıyla güncellenebilmeli")
    void shouldUpdateWidget() {
        UUID widgetId = testWidget.getId();
        WidgetRequest request = new WidgetRequest();
        request.setName("Aktif Kullanıcı Sayısı");
        request.setDescription("Şu an çevrimiçi olan kullanıcı sayısı");
        request.setWidgetType(WidgetType.COUNTER);
        request.setDataSourceType(DataSourceType.SQL);
        request.setDataSource("SELECT COUNT(*) FROM users WHERE active = true");
        request.setRefreshInterval(60);

        when(widgetRepository.findById(widgetId)).thenReturn(Optional.of(testWidget));
        when(widgetRepository.save(any(Widget.class))).thenReturn(testWidget);

        WidgetResponse response = widgetService.update(widgetId, request);

        assertThat(response).isNotNull();
        verify(widgetRepository).save(any(Widget.class));
    }

    @Test
    @DisplayName("Widget soft delete yapılmalı")
    void shouldSoftDeleteWidget() {
        UUID widgetId = testWidget.getId();
        when(widgetRepository.findById(widgetId)).thenReturn(Optional.of(testWidget));
        when(widgetRepository.save(any(Widget.class))).thenReturn(testWidget);

        widgetService.delete(widgetId);

        assertThat(testWidget.getActive()).isFalse();
        verify(widgetRepository).save(testWidget);
    }

    @Test
    @DisplayName("Widget API veri kaynağı ile oluşturulabilmeli")
    void shouldCreateWidgetWithApiDataSource() {
        setSecurityContext();

        WidgetRequest request = new WidgetRequest();
        request.setName("İstatistikler");
        request.setDescription("Harici API'den veri alan widget");
        request.setWidgetType(WidgetType.BAR_CHART);
        request.setDataSourceType(DataSourceType.REST_API);
        request.setDataSource("https://api.example.com/stats");
        request.setRefreshInterval(600);

        when(widgetRepository.save(any(Widget.class))).thenAnswer(inv -> {
            Widget w = inv.getArgument(0);
            w.setId(UUID.randomUUID());
            return w;
        });

        WidgetResponse response = widgetService.create(request);

        assertThat(response.getDataSourceType()).isEqualTo(DataSourceType.REST_API);
        assertThat(response.getDataSource()).isEqualTo("https://api.example.com/stats");
        verify(widgetRepository).save(any(Widget.class));
    }

    @Test
    @DisplayName("Widget statik veri kaynağı ile oluşturulabilmeli")
    void shouldCreateWidgetWithStaticDataSource() {
        setSecurityContext();

        WidgetRequest request = new WidgetRequest();
        request.setName("Sabit İçerik");
        request.setDescription("Statik veri ile oluşturulan widget");
        request.setWidgetType(WidgetType.TABLE);
        request.setDataSourceType(DataSourceType.STATIC);
        request.setDataSource("{\"data\": [1, 2, 3]}");

        when(widgetRepository.save(any(Widget.class))).thenAnswer(inv -> {
            Widget w = inv.getArgument(0);
            w.setId(UUID.randomUUID());
            return w;
        });

        WidgetResponse response = widgetService.create(request);

        assertThat(response.getDataSourceType()).isEqualTo(DataSourceType.STATIC);
        verify(widgetRepository).save(any(Widget.class));
    }

    @Test
    @DisplayName("Widget sıralanmış listesi ad'a göre dönmeli")
    void shouldReturnWidgetsSortedByName() {
        setSecurityContext();

        Widget widget1 = new Widget();
        widget1.setId(UUID.randomUUID());
        widget1.setName("A Widget");
        widget1.setCompanyId("company1");
        widget1.setActive(true);

        Widget widget2 = new Widget();
        widget2.setId(UUID.randomUUID());
        widget2.setName("B Widget");
        widget2.setCompanyId("company1");
        widget2.setActive(true);

        when(widgetRepository.findByCompanyIdAndActiveOrderByNameAsc("company1", true))
                .thenReturn(List.of(widget1, widget2));

        List<WidgetResponse> result = widgetService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("A Widget");
        assertThat(result.get(1).getName()).isEqualTo("B Widget");
    }
}
