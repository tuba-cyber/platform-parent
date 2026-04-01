package com.platform.core.notification.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.platform.core.notification.dto.NotificationRequest;
import com.platform.core.notification.dto.NotificationResponse;
import com.platform.core.notification.entity.Notification;
import com.platform.core.notification.entity.NotificationType;
import com.platform.core.notification.repository.NotificationRepository;
import com.platform.core.security.model.UserPrincipal;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private UserPrincipal testPrincipal;

    @BeforeEach
    void setUp() {
        testPrincipal = UserPrincipal.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .password("")
                .companyId("company1")
                .roles(List.of("USER"))
                .permissions(List.of())
                .active(true)
                .build();
    }

    private void setSecurityContext() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(testPrincipal, null, testPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("Bildirim başarıyla gönderilmeli")
    void shouldSendNotification() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(new Notification());

        NotificationRequest request = new NotificationRequest();
        request.setTitle("Test Bildirim");
        request.setMessage("Test mesajı");
        request.setType(NotificationType.INFO);
        request.setTargetUsername("targetuser");

        notificationService.send(request, "company1");

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Hızlı bildirim gönderilmeli")
    void shouldSendQuickNotification() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(new Notification());

        notificationService.sendQuick("targetuser", "company1",
                "Hızlı Bildirim", "Test mesajı", NotificationType.WARNING);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Kullanıcının bildirimleri getirilmeli")
    void shouldGetMyNotifications() {
        setSecurityContext();

        Notification notification = createTestNotification();
        when(notificationRepository.findByTargetUsernameAndCompanyIdOrderByCreatedAtDesc(
                "testuser", "company1"))
                .thenReturn(List.of(notification));

        List<NotificationResponse> result = notificationService.getMyNotifications();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Bildirim");
    }

    @Test
    @DisplayName("Okunmamış bildirimler getirilmeli")
    void shouldGetUnreadNotifications() {
        setSecurityContext();

        Notification notification = createTestNotification();
        when(notificationRepository.findByTargetUsernameAndCompanyIdAndIsReadOrderByCreatedAtDesc(
                "testuser", "company1", false))
                .thenReturn(List.of(notification));

        List<NotificationResponse> result = notificationService.getUnreadNotifications();

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Okunmamış bildirim sayısı dönmeli")
    void shouldGetUnreadCount() {
        setSecurityContext();

        when(notificationRepository.countByTargetUsernameAndCompanyIdAndIsRead(
                "testuser", "company1", false))
                .thenReturn(5L);

        Long count = notificationService.getUnreadCount();
        assertThat(count).isEqualTo(5);
    }

    @Test
    @DisplayName("Bildirim okundu olarak işaretlenmeli")
    void shouldMarkAsRead() {
        UUID notificationId = UUID.randomUUID();
        Notification notification = createTestNotification();
        notification.setId(notificationId);

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        NotificationResponse result = notificationService.markAsRead(notificationId);

        assertThat(result.getTitle()).isEqualTo("Test Bildirim");
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Bildirim bulunamazsa hata fırlatmalı")
    void shouldThrowWhenNotificationNotFound() {
        UUID notificationId = UUID.randomUUID();
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.markAsRead(notificationId))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Tüm bildirimler okundu olarak işaretlenmeli")
    void shouldMarkAllAsRead() {
        setSecurityContext();

        when(notificationRepository.markAllAsRead("testuser", "company1")).thenReturn(3);

        int count = notificationService.markAllAsRead();
        assertThat(count).isEqualTo(3);
    }

    private Notification createTestNotification() {
        Notification n = new Notification();
        n.setId(UUID.randomUUID());
        n.setTitle("Test Bildirim");
        n.setMessage("Test mesajı");
        n.setType(NotificationType.INFO);
        n.setTargetUsername("testuser");
        n.setCompanyId("company1");
        n.setIsRead(false);
        n.setCreatedAt(LocalDateTime.now());
        return n;
    }
}
