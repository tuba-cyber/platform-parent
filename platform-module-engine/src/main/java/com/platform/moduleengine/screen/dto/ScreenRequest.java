package com.platform.moduleengine.screen.dto;

import com.platform.moduleengine.screen.entity.ScreenType;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScreenRequest {

    @NotBlank(message = "Ekran adı boş olamaz")
    private String name;

    @NotBlank(message = "Ekran kodu boş olamaz")
    private String code;

    private String description;
    private String icon;
    private Integer orderIndex = 0;
    private Boolean visible = true;
    private String template;
    private ScreenType screenType = ScreenType.CUSTOM;
    private String requiredPermission;
}