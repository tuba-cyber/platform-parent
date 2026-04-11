package com.platform.hr.position.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class PositionRequest {

    @NotNull(message = "Şirket ID zorunludur")
    private UUID companyId;

    @NotBlank(message = "Unvan adı zorunludur")
    private String title;

    @NotBlank(message = "Pozisyon kodu zorunludur")
    private String code;

    private String description;
    private UUID departmentId;
    private String level;
}
