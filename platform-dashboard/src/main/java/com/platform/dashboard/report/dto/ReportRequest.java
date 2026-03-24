package com.platform.dashboard.report.dto;

import com.platform.dashboard.report.entity.ReportType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequest {

    @NotBlank(message = "Rapor adı boş olamaz")
    private String name;

    private String description;

    @NotNull(message = "Rapor tipi boş olamaz")
    private ReportType reportType;

    @NotBlank(message = "Sorgu boş olamaz")
    private String query;

    private String columnDefinitions;
    private String filterDefinitions;
    private String requiredPermission;
}