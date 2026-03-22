package com.platform.cbs.layer.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.platform.cbs.layer.entity.LayerType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LayerResponse {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private LayerType layerType;
    private String geoserverWorkspace;
    private String geoserverLayerName;
    private String wmsUrl;
    private String wfsUrl;
    private String defaultStyle;
    private String color;
    private String icon;
    private Integer orderIndex;
    private Boolean visible;
    private Boolean queryable;
    private Boolean active;
    private String requiredPermission;
    private String companyId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}