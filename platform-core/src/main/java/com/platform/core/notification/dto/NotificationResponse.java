package com.platform.core.notification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.platform.core.notification.entity.NotificationType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponse {

    private UUID id;
    private String title;
    private String message;
    private NotificationType type;
    private String targetUsername;
    private String companyId;
    private Boolean isRead;
    private LocalDateTime readAt;
    private String entityType;
    private String entityId;
    private String actionUrl;
    private LocalDateTime createdAt;
}