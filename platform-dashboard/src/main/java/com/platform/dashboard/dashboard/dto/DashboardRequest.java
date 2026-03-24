package com.platform.dashboard.dashboard.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardRequest {

    @NotBlank(message = "Dashboard adı boş olamaz")
    private String name;

    private String description;
    private String icon;
    private Integer orderIndex = 0;
    private Boolean isDefault = false;
    private String requiredPermission;
}