package com.example.ConnectUs.dto.notification;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class NotificationResponse {
    private Integer id;
    private String firstname;
    private String lastname;
    private String type;
    private String text;
    private String avatar;
    private Integer entityId;
    private String dateAndTime;
    private Integer requestId;
}
