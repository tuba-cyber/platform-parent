package com.platform.cbs.feature.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GeoJsonFeature {

    private final String type = "Feature";
    private String id;
    private Object geometry;
    private Map<String, Object> properties;
}