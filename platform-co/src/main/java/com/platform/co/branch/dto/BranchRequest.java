package com.platform.co.branch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class BranchRequest {

    @NotNull(message = "Şirket ID zorunludur")
    private UUID companyId;

    @NotBlank(message = "Şube adı zorunludur")
    private String name;

    @NotBlank(message = "Şube kodu zorunludur")
    private String code;

    private String address;
    private String city;
    private String district;
    private String postalCode;
    private String phone;
    private String email;
    private Boolean headquarters = false;
}
