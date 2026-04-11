package com.platform.hr.department.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class DepartmentRequest {

    @NotNull(message = "Şirket ID zorunludur")
    private UUID companyId;

    @NotBlank(message = "Departman adı zorunludur")
    private String name;

    @NotBlank(message = "Departman kodu zorunludur")
    private String code;

    private String description;

    private UUID parentDepartmentId;
}
