package com.platform.moduleengine.screen.dto;

import java.util.UUID;

import com.platform.moduleengine.screen.entity.ScreenType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScreenResponse {

    private UUID id;
    private String name;
    private String code;
    private String description;
    private String icon;
    private Integer orderIndex;
    private Boolean visible;
    private Boolean active;
    private String template;
    private ScreenType screenType;
    private String requiredPermission;
    private UUID sectionId;
}