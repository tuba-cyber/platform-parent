package com.platform.co.branch.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BranchResponse {
    private UUID id;
    private UUID companyId;
    private String name;
    private String code;
    private String address;
    private String city;
    private String district;
    private String postalCode;
    private String phone;
    private String email;
    private Boolean headquarters;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
