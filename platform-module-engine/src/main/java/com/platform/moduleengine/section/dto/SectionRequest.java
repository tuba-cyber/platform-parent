package com.platform.moduleengine.section.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectionRequest {

    @NotBlank(message = "Bölüm adı boş olamaz")
    private String name;

    private String description;
    private String icon;
    private Integer orderIndex = 0;
    private Boolean visible = true;
}