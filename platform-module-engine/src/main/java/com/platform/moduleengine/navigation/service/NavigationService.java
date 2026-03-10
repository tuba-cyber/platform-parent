package com.platform.moduleengine.navigation.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.core.security.model.UserPrincipal;
import com.platform.moduleengine.navigation.dto.NavigationResponse;
import com.platform.moduleengine.navigation.entity.NavigationItem;
import com.platform.moduleengine.navigation.repository.NavigationItemRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NavigationService {

    private final NavigationItemRepository navigationItemRepository;

    @Transactional(readOnly = true)
    public List<NavigationResponse> getNavigationForCurrentUser() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder
            .getContext().getAuthentication().getPrincipal();

        String companyId = principal.getCompanyId();
        List<String> permissions = principal.getPermissions();

        return navigationItemRepository
            .findRootItemsByCompanyId(companyId)
            .stream()
            .filter(item -> canAccess(item, permissions))
            .map(item -> toResponse(item, permissions))
            .collect(Collectors.toList());
    }

    private boolean canAccess(NavigationItem item, List<String> permissions) {
        if (item.getRequiredPermission() == null ||
            item.getRequiredPermission().isEmpty()) {
            return true;
        }
        return permissions.contains(item.getRequiredPermission());
    }

    private NavigationResponse toResponse(
            NavigationItem item, List<String> permissions) {
        return NavigationResponse.builder()
                .id(item.getId())
                .label(item.getLabel())
                .icon(item.getIcon())
                .orderIndex(item.getOrderIndex())
                .navigationType(item.getNavigationType())
                .moduleId(item.getModuleId())
                .screenCode(item.getScreenCode())
                .externalUrl(item.getExternalUrl())
                .children(item.getChildren() != null
                    ? item.getChildren().stream()
                        .filter(child -> canAccess(child, permissions))
                        .map(child -> toResponse(child, permissions))
                        .collect(Collectors.toList())
                    : null)
                .build();
    }
}