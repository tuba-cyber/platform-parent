package com.platform.moduleengine.module.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.platform.moduleengine.section.dto.SectionResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ModuleResponse {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private String icon;
    private String color;
    private Integer orderIndex;
    private Boolean visible;
    private Boolean active;
    private String companyId;
    private List<SectionResponse> sections;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}