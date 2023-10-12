package com.example.ConnectUs.service;

import com.example.ConnectUs.dto.notification.NotificationResponse;
import com.example.ConnectUs.model.postgres.Notification;
import com.example.ConnectUs.repository.postgres.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification setIsRead(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    public void deleteNotification(Integer notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    public List<NotificationResponse> getAllNotifications(Integer userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        List<NotificationResponse> notificationResponses = new ArrayList<>();

        for (Notification n : notifications) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = n.getDateAndTime().format(formatter);

            notificationResponses.add(NotificationResponse.builder()
                    .text(n.getText())
                    .type(n.getType().toString())
                    .firstname(n.getFirstname())
                    .lastname(n.getLastname())
                    .avatar(n.getAvatar())
                    .dateAndTime(formattedDateTime)
                    .entityId(n.getEntityId())
                    .id(n.getId())
                    .requestId(n.getRequestId())
                    .build());
        }

        return notificationResponses;
    }

    public List<NotificationResponse> getAllUnreadNotifications(Integer userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
        List<NotificationResponse> notificationResponses = new ArrayList<>();

        for (Notification n : notifications) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = n.getDateAndTime().format(formatter);

            notificationResponses.add(NotificationResponse.builder()
                    .text(n.getText())
                    .type(n.getType().toString())
                    .firstname(n.getFirstname())
                    .lastname(n.getLastname())
                    .avatar(n.getAvatar())
                    .dateAndTime(formattedDateTime)
                    .entityId(n.getEntityId())
                    .id(n.getId())
                    .requestId(n.getRequestId())
                    .build());
        }

        return notificationResponses;
    }
}
