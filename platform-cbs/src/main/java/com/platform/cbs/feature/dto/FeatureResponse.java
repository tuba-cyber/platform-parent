package com.platform.cbs.feature.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.platform.cbs.feature.entity.FeatureType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeatureResponse {

    private UUID id;
    private String name;
    private String description;
    private FeatureType featureType;
    private String geometryGeoJson;
    private String properties;
    private Boolean active;
    private String companyId;
    private UUID layerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}