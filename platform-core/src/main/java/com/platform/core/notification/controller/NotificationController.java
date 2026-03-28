package com.platform.core.notification.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.core.common.response.ApiResponse;
import com.platform.core.notification.dto.NotificationRequest;
import com.platform.core.notification.dto.NotificationResponse;
import com.platform.core.notification.service.NotificationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>>
            getMyNotifications() {
        return ResponseEntity.ok(
            ApiResponse.success(notificationService.getMyNotifications())
        );
    }

    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>>
            getUnread() {
        return ResponseEntity.ok(
            ApiResponse.success(notificationService.getUnreadNotifications())
        );
    }

    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount() {
        return ResponseEntity.ok(
            ApiResponse.success(notificationService.getUnreadCount())
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> send(
            @Valid @RequestBody NotificationRequest request) {
        com.platform.core.security.model.UserPrincipal principal =
            (com.platform.core.security.model.UserPrincipal)
            org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        notificationService.send(request, principal.getCompanyId());
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(null, "Bildirim gönderildi")
        );
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            @PathVariable("id") UUID id) {
        return ResponseEntity.ok(
            ApiResponse.success(
                notificationService.markAsRead(id), "Okundu olarak işaretlendi"
            )
        );
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Integer>> markAllAsRead() {
        return ResponseEntity.ok(
            ApiResponse.success(
                notificationService.markAllAsRead(), "Tümü okundu olarak işaretlendi"
            )
        );
    }
}