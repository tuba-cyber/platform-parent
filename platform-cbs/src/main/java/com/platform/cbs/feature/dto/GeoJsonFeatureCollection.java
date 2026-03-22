package com.platform.cbs.feature.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GeoJsonFeatureCollection {

    private final String type = "FeatureCollection";
    private List<GeoJsonFeature> features;
    private Integer totalCount;
}