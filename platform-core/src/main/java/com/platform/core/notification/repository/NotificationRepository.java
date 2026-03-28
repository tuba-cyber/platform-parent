package com.platform.core.notification.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.platform.core.notification.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByTargetUsernameAndCompanyIdOrderByCreatedAtDesc(
        String targetUsername, String companyId
    );

    List<Notification> findByTargetUsernameAndCompanyIdAndIsReadOrderByCreatedAtDesc(
        String targetUsername, String companyId, Boolean isRead
    );

    Long countByTargetUsernameAndCompanyIdAndIsRead(
        String targetUsername, String companyId, Boolean isRead
    );

    @Modifying
    @Query("""
        UPDATE Notification n
        SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP
        WHERE n.targetUsername = :username
        AND n.companyId = :companyId
        AND n.isRead = false
    """)
    int markAllAsRead(
        @Param("username") String username,
        @Param("companyId") String companyId
    );
}