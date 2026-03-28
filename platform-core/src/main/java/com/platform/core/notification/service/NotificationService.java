package com.platform.core.notification.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.core.notification.dto.NotificationRequest;
import com.platform.core.notification.dto.NotificationResponse;
import com.platform.core.notification.entity.Notification;
import com.platform.core.notification.entity.NotificationType;
import com.platform.core.notification.repository.NotificationRepository;
import com.platform.core.security.model.UserPrincipal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // Bildirim gönder
    @Async
    public void send(NotificationRequest request, String companyId) {
        try {
            Notification notification = new Notification();
            notification.setTitle(request.getTitle());
            notification.setMessage(request.getMessage());
            notification.setType(request.getType());
            notification.setTargetUsername(request.getTargetUsername());
            notification.setCompanyId(companyId);
            notification.setEntityType(request.getEntityType());
            notification.setEntityId(request.getEntityId());
            notification.setActionUrl(request.getActionUrl());

            notificationRepository.save(notification);
            log.info("Bildirim gönderildi: {} → {}",
                request.getTitle(), request.getTargetUsername());
        } catch (Exception e) {
            log.error("Bildirim gönderilemedi: {}", e.getMessage());
        }
    }

    // Hızlı bildirim gönder
    @Async
    public void sendQuick(String targetUsername, String companyId,
            String title, String message, NotificationType type) {
        try {
            Notification notification = new Notification();
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setType(type);
            notification.setTargetUsername(targetUsername);
            notification.setCompanyId(companyId);

            notificationRepository.save(notification);
            log.info("Bildirim gönderildi: {} → {}", title, targetUsername);
        } catch (Exception e) {
            log.error("Bildirim gönderilemedi: {}", e.getMessage());
        }
    }

    // Kullanıcının bildirimlerini getir
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications() {
        UserPrincipal principal = getCurrentUser();
        return notificationRepository
            .findByTargetUsernameAndCompanyIdOrderByCreatedAtDesc(
                principal.getUsername(), principal.getCompanyId())
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    // Okunmamış bildirimleri getir
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications() {
        UserPrincipal principal = getCurrentUser();
        return notificationRepository
            .findByTargetUsernameAndCompanyIdAndIsReadOrderByCreatedAtDesc(
                principal.getUsername(), principal.getCompanyId(), false)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    // Okunmamış bildirim sayısı
    @Transactional(readOnly = true)
    public Long getUnreadCount() {
        UserPrincipal principal = getCurrentUser();
        return notificationRepository
            .countByTargetUsernameAndCompanyIdAndIsRead(
                principal.getUsername(), principal.getCompanyId(), false);
    }

    // Bildirimi okundu olarak işaretle
    @Transactional
    public NotificationResponse markAsRead(UUID id) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Bildirim bulunamadı"));
        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
        return toResponse(notification);
    }

    // Tümünü okundu olarak işaretle
    @Transactional
    public int markAllAsRead() {
        UserPrincipal principal = getCurrentUser();
        return notificationRepository.markAllAsRead(
            principal.getUsername(), principal.getCompanyId());
    }

    private UserPrincipal getCurrentUser() {
        return (UserPrincipal) SecurityContextHolder
            .getContext().getAuthentication().getPrincipal();
    }

    public NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .targetUsername(notification.getTargetUsername())
                .companyId(notification.getCompanyId())
                .isRead(notification.getIsRead())
                .readAt(notification.getReadAt())
                .entityType(notification.getEntityType())
                .entityId(notification.getEntityId())
                .actionUrl(notification.getActionUrl())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}