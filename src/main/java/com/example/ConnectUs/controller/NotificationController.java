package com.example.ConnectUs.controller;

import com.example.ConnectUs.dto.notification.NotificationResponse;
import com.example.ConnectUs.exceptions.DatabaseAccessException;
import com.example.ConnectUs.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(@RequestParam Integer userId){
        try{
            return ResponseEntity.ok(notificationService.getAllNotifications(userId));
        }catch (DatabaseAccessException e){
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/getAllUnreadNotifications")
    public ResponseEntity<List<NotificationResponse>> getAllReadNotifications(@RequestParam Integer userId){
        try{
            return ResponseEntity.ok(notificationService.getAllUnreadNotifications(userId));
        }catch (DatabaseAccessException e){
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping
    public ResponseEntity setIsRead(@RequestParam Integer notificationId){
        try{
            notificationService.setIsRead(notificationId);
            return ResponseEntity.status(200).body(true);
        }catch (DatabaseAccessException e){
            return ResponseEntity.status(500).body(null);
        }
    }
}
