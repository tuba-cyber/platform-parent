package com.platform.hr.department.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DepartmentResponse {
    private UUID id;
    private UUID companyId;
    private String name;
    private String code;
    private String description;
    private UUID parentDepartmentId;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
