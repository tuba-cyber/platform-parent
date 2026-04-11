package com.platform.hr.position.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PositionResponse {
    private UUID id;
    private UUID companyId;
    private String title;
    private String code;
    private String description;
    private UUID departmentId;
    private String level;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
