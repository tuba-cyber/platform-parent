package com.platform.cbs.feature.dto;

import com.platform.cbs.feature.entity.FeatureType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeatureRequest {

    @NotBlank(message = "Feature adı boş olamaz")
    private String name;

    private String description;

    @NotNull(message = "Feature tipi boş olamaz")
    private FeatureType featureType;

    // GeoJSON formatında geometry
    @NotBlank(message = "Geometri boş olamaz")
    private String geometryGeoJson;

    // JSON formatında özellikler
    private String properties;

    @NotNull(message = "Katman ID boş olamaz")
    private String layerId;
}