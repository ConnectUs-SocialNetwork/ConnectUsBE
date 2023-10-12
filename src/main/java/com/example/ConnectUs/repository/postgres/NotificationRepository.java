package com.example.ConnectUs.repository.postgres;

import com.example.ConnectUs.model.postgres.Notification;
import com.example.ConnectUs.model.postgres.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    Optional<Notification> findById(Integer id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.type = 5 AND n.entityId = :entityId")
    void deleteFriendRequestNotificationsByEntityId(@Param("entityId") Integer entityId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.type = 5 AND n.requestId = :requestId")
    void deleteFriendRequestNotificationsByRequestId(@Param("requestId") Integer requestId);

    List<Notification> findByUserId(Integer userId);

    List<Notification> findByUserIdAndIsReadFalse(Integer user);
}
