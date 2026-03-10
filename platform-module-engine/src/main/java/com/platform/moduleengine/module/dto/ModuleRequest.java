package com.platform.moduleengine.module.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModuleRequest {

    @NotBlank(message = "Modül kodu boş olamaz")
    @Size(max = 50, message = "Modül kodu en fazla 50 karakter olabilir")
    private String code;

    @NotBlank(message = "Modül adı boş olamaz")
    private String name;

    private String description;
    private String icon;
    private String color;
    private Integer orderIndex = 0;
    private Boolean visible = true;
}