package com.platform.moduleengine.navigation.dto;

import java.util.List;
import java.util.UUID;

import com.platform.moduleengine.navigation.entity.NavigationType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NavigationResponse {

    private UUID id;
    private String label;
    private String icon;
    private Integer orderIndex;
    private NavigationType navigationType;
    private String moduleId;
    private String screenCode;
    private String externalUrl;
    private List<NavigationResponse> children;
}