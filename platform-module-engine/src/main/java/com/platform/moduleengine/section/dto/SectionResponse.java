package com.platform.moduleengine.section.dto;

import java.util.List;
import java.util.UUID;

import com.platform.moduleengine.screen.dto.ScreenResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SectionResponse {

    private UUID id;
    private String name;
    private String description;
    private String icon;
    private Integer orderIndex;
    private Boolean visible;
    private Boolean active;
    private UUID moduleId;
    private List<ScreenResponse> screens;
}