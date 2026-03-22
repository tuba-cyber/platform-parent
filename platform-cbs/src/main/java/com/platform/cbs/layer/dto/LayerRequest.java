package com.platform.cbs.layer.dto;

import com.platform.cbs.layer.entity.LayerType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LayerRequest {

    @NotBlank(message = "Katman kodu boş olamaz")
    private String code;

    @NotBlank(message = "Katman adı boş olamaz")
    private String name;

    private String description;

    @NotNull(message = "Katman tipi boş olamaz")
    private LayerType layerType;

    private String geoserverWorkspace;
    private String geoserverLayerName;
    private String wmsUrl;
    private String wfsUrl;
    private String defaultStyle;
    private String color;
    private String icon;
    private Integer orderIndex = 0;
    private Boolean visible = true;
    private Boolean queryable = true;
    private String requiredPermission;
}