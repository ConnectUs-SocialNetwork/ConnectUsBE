package com.example.ConnectUs.dto.notification;

import com.example.ConnectUs.dto.post.PostResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class NotificationResponse implements Comparable<NotificationResponse>{
    private Integer id;
    private String firstname;
    private String lastname;
    private String type;
    private String text;
    private String avatar;
    private Integer entityId;
    private String dateAndTime;
    private Integer requestId;

    @Override
    public int compareTo(NotificationResponse notificationResponse) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime1 = LocalDateTime.parse(getDateAndTime(), formatter);
        LocalDateTime dateTime2 = LocalDateTime.parse(notificationResponse.getDateAndTime(), formatter);

        return dateTime1.compareTo(dateTime2);
    }
}
