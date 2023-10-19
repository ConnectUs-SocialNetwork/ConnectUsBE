package com.example.ConnectUs.model.postgres;

import com.example.ConnectUs.enumerations.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue
    private Integer id;
    private String text;
    private NotificationType type;
    private Integer entityId;
    @Column(name = "avatar", columnDefinition = "TEXT")
    private String avatar;
    private LocalDateTime dateAndTime;
    private boolean isRead;
    private String firstname;
    private String lastname;
    private Integer requestId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
