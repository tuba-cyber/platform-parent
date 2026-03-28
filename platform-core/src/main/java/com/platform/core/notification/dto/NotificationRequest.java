package com.platform.core.notification.dto;

import com.platform.core.notification.entity.NotificationType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequest {

    @NotBlank(message = "Başlık boş olamaz")
    private String title;

    @NotBlank(message = "Mesaj boş olamaz")
    private String message;

    @NotNull(message = "Tip boş olamaz")
    private NotificationType type;

    @NotBlank(message = "Hedef kullanıcı boş olamaz")
    private String targetUsername;

    private String entityType;
    private String entityId;
    private String actionUrl;
}