package com.platform.dashboard.report.service;

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
import com.platform.dashboard.report.dto.ReportRequest;
import com.platform.dashboard.report.dto.ReportResponse;
import com.platform.dashboard.report.entity.Report;
import com.platform.dashboard.report.entity.ReportType;
import com.platform.dashboard.report.repository.ReportRepository;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportService reportService;

    private UserPrincipal testPrincipal;
    private Report testReport;

    @BeforeEach
    void setUp() {
        testPrincipal = UserPrincipal.builder()
                .id(UUID.randomUUID())
                .username("admin")
                .password("")
                .companyId("company1")
                .roles(List.of("ADMIN"))
                .permissions(List.of("REPORT_MANAGE"))
                .active(true)
                .build();

        testReport = new Report();
        testReport.setId(UUID.randomUUID());
        testReport.setName("Aylık Özet Raporu");
        testReport.setDescription("Aylık satış ve gelir özeti");
        testReport.setReportType(ReportType.MIXED);
        testReport.setQuery("SELECT DATE_TRUNC('month', created_at) as month, COUNT(*) as count, SUM(amount) as total FROM orders GROUP BY month");
        testReport.setCompanyId("company1");
        testReport.setActive(true);
    }

    private void setSecurityContext() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(testPrincipal, null, testPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("Rapor başarıyla oluşturulmalı")
    void shouldCreateReport() {
        setSecurityContext();

        ReportRequest request = new ReportRequest();
        request.setName("Aylık Özet Raporu");
        request.setDescription("Aylık satış ve gelir özeti");
        request.setReportType(ReportType.MIXED);
        request.setQuery("SELECT DATE_TRUNC('month', created_at) as month, COUNT(*) as count, SUM(amount) as total FROM orders GROUP BY month");

        when(reportRepository.save(any(Report.class))).thenAnswer(inv -> {
            Report r = inv.getArgument(0);
            r.setId(UUID.randomUUID());
            return r;
        });

        ReportResponse response = reportService.create(request);

        assertThat(response.getName()).isEqualTo("Aylık Özet Raporu");
        assertThat(response.getReportType()).isEqualTo(ReportType.MIXED);
        assertThat(response.getCompanyId()).isEqualTo("company1");
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    @DisplayName("Tüm raporlar listelenebilmeli")
    void shouldGetAllReports() {
        setSecurityContext();

        when(reportRepository.findByCompanyIdAndActiveOrderByNameAsc("company1", true))
                .thenReturn(List.of(testReport));

        List<ReportResponse> result = reportService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Aylık Özet Raporu");
        assertThat(result.get(0).getReportType()).isEqualTo(ReportType.MIXED);
    }

    @Test
    @DisplayName("Rapor ID ile getirilmeli")
    void shouldGetReportById() {
        UUID reportId = testReport.getId();
        when(reportRepository.findById(reportId)).thenReturn(Optional.of(testReport));

        ReportResponse response = reportService.getById(reportId);

        assertThat(response.getName()).isEqualTo("Aylık Özet Raporu");
        assertThat(response.getQuery()).contains("DATE_TRUNC");
    }

    @Test
    @DisplayName("Rapor bulunamazsa hata fırlatmalı")
    void shouldThrowWhenReportNotFound() {
        UUID id = UUID.randomUUID();
        when(reportRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reportService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Rapor");
    }

    @Test
    @DisplayName("Rapor başarıyla güncellenebilmeli")
    void shouldUpdateReport() {
        UUID reportId = testReport.getId();
        ReportRequest request = new ReportRequest();
        request.setName("Güncellenmiş Aylık Raporu");
        request.setDescription("Güncellenmiş açıklama");
        request.setReportType(ReportType.MIXED);
        request.setQuery("SELECT * FROM updated_orders");

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(testReport));
        when(reportRepository.save(any(Report.class))).thenReturn(testReport);

        ReportResponse response = reportService.update(reportId, request);

        assertThat(response).isNotNull();
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    @DisplayName("Rapor soft delete yapılmalı")
    void shouldSoftDeleteReport() {
        UUID reportId = testReport.getId();
        when(reportRepository.findById(reportId)).thenReturn(Optional.of(testReport));
        when(reportRepository.save(any(Report.class))).thenReturn(testReport);

        reportService.delete(reportId);

        assertThat(testReport.getActive()).isFalse();
        verify(reportRepository).save(testReport);
    }

    @Test
    @DisplayName("Tablo tipi rapor oluşturulabilmeli")
    void shouldCreateTableReport() {
        setSecurityContext();

        ReportRequest request = new ReportRequest();
        request.setName("Kullanıcılar Tablosu");
        request.setDescription("Tüm kullanıcıların listesi");
        request.setReportType(ReportType.TABLE);
        request.setQuery("SELECT id, username, email, created_at FROM users ORDER BY created_at DESC");
        request.setColumnDefinitions("[{\"name\": \"id\", \"label\": \"ID\"}, {\"name\": \"username\", \"label\": \"Kullanıcı Adı\"}]");

        when(reportRepository.save(any(Report.class))).thenAnswer(inv -> {
            Report r = inv.getArgument(0);
            r.setId(UUID.randomUUID());
            return r;
        });

        ReportResponse response = reportService.create(request);

        assertThat(response.getReportType()).isEqualTo(ReportType.TABLE);
        assertThat(response.getColumnDefinitions()).contains("id");
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    @DisplayName("Grafik tipi rapor oluşturulabilmeli")
    void shouldCreateChartReport() {
        setSecurityContext();

        ReportRequest request = new ReportRequest();
        request.setName("Satış Grafiği");
        request.setDescription("Aylık satış trendleri");
        request.setReportType(ReportType.CHART);
        request.setQuery("SELECT month, total_sales FROM monthly_sales");
        request.setColumnDefinitions("[{\"name\": \"month\", \"type\": \"string\"}, {\"name\": \"total_sales\", \"type\": \"number\"}]");

        when(reportRepository.save(any(Report.class))).thenAnswer(inv -> {
            Report r = inv.getArgument(0);
            r.setId(UUID.randomUUID());
            return r;
        });

        ReportResponse response = reportService.create(request);

        assertThat(response.getReportType()).isEqualTo(ReportType.CHART);
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    @DisplayName("Rapor filtre tanımlamalarıyla oluşturulabilmeli")
    void shouldCreateReportWithFilters() {
        setSecurityContext();

        ReportRequest request = new ReportRequest();
        request.setName("Filtrelenmiş Rapor");
        request.setDescription("Tarih ve duruma göre filtrelenebilen rapor");
        request.setReportType(ReportType.TABLE);
        request.setQuery("SELECT * FROM orders WHERE created_at >= :startDate AND created_at <= :endDate AND status = :status");
        request.setFilterDefinitions("[{\"name\": \"startDate\", \"label\": \"Başlangıç Tarihi\", \"type\": \"date\"}, {\"name\": \"endDate\", \"label\": \"Bitiş Tarihi\", \"type\": \"date\"}, {\"name\": \"status\", \"label\": \"Durum\", \"type\": \"select\"}]");

        when(reportRepository.save(any(Report.class))).thenAnswer(inv -> {
            Report r = inv.getArgument(0);
            r.setId(UUID.randomUUID());
            return r;
        });

        ReportResponse response = reportService.create(request);

        assertThat(response.getFilterDefinitions()).contains("startDate");
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    @DisplayName("Raporlar ad'a göre sıralanmalı")
    void shouldReturnReportsSortedByName() {
        setSecurityContext();

        Report report1 = new Report();
        report1.setId(UUID.randomUUID());
        report1.setName("A Raporu");
        report1.setCompanyId("company1");
        report1.setActive(true);

        Report report2 = new Report();
        report2.setId(UUID.randomUUID());
        report2.setName("B Raporu");
        report2.setCompanyId("company1");
        report2.setActive(true);

        when(reportRepository.findByCompanyIdAndActiveOrderByNameAsc("company1", true))
                .thenReturn(List.of(report1, report2));

        List<ReportResponse> result = reportService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("A Raporu");
        assertThat(result.get(1).getName()).isEqualTo("B Raporu");
    }

    @Test
    @DisplayName("İzin gerektiren rapor oluşturulabilmeli")
    void shouldCreateReportWithRequiredPermission() {
        setSecurityContext();

        ReportRequest request = new ReportRequest();
        request.setName("Gizli Rapor");
        request.setDescription("Sadece yöneticilere görünür");
        request.setReportType(ReportType.MIXED);
        request.setQuery("SELECT * FROM sensitive_data");
        request.setRequiredPermission("ADMIN_REPORT_VIEW");

        when(reportRepository.save(any(Report.class))).thenAnswer(inv -> {
            Report r = inv.getArgument(0);
            r.setId(UUID.randomUUID());
            return r;
        });

        ReportResponse response = reportService.create(request);

        assertThat(response.getRequiredPermission()).isEqualTo("ADMIN_REPORT_VIEW");
        verify(reportRepository).save(any(Report.class));
    }
}
