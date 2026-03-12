package com.platform.moduleengine.navigation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.core.common.response.ApiResponse;
import com.platform.moduleengine.navigation.dto.NavigationResponse;
import com.platform.moduleengine.navigation.service.NavigationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/navigation")
@RequiredArgsConstructor
public class NavigationController {

    private final NavigationService navigationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NavigationResponse>>> getNavigation() {
        return ResponseEntity.ok(
            ApiResponse.success(
                navigationService.getNavigationForCurrentUser()
            )
        );
    }
}