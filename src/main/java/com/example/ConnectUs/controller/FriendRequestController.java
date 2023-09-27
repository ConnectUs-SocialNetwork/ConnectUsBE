package com.example.ConnectUs.controller;

import com.example.ConnectUs.dto.friendRequest.FriendRequestDTO;
import com.example.ConnectUs.dto.friendRequest.FriendRequestResponse;
import com.example.ConnectUs.dto.friendRequest.ProcessFriendRequestResponse;
import com.example.ConnectUs.dto.friendRequest.ProcessRequestDTO;
import com.example.ConnectUs.exceptions.DatabaseAccessException;
import com.example.ConnectUs.model.postgres.FriendRequest;
import com.example.ConnectUs.service.FriendRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/friendRequest")
@RequiredArgsConstructor
public class FriendRequestController {
    private final FriendRequestService friendRequestService;

    @PostMapping("/addFriend")
    public ResponseEntity<FriendRequestResponse> addFriend(
            @RequestBody FriendRequestDTO friendRequest
    ) {
        boolean success = false;
        try {
            success = friendRequestService.addFriend(friendRequest.getUserId(), friendRequest.getFriendId());
            return ResponseEntity.ok(FriendRequestResponse.builder().success(success).build());
        } catch (DataAccessException e) {
            return ResponseEntity.status(500).body(FriendRequestResponse.builder().success(success).build());
        }
    }

    @PutMapping("/processRequest")
    public ResponseEntity<ProcessFriendRequestResponse> processRequest(@RequestBody ProcessRequestDTO processRequestDTO){
        try{
            FriendRequest friendRequest = friendRequestService.processRequest(processRequestDTO);
            ProcessFriendRequestResponse res = ProcessFriendRequestResponse.builder()
                    .id(friendRequest.getId())
                    .status(friendRequest.getStatus())
                    .build();
            return ResponseEntity.ok(res);
        }catch (DatabaseAccessException e){
            return ResponseEntity.status(500).body(null);
        }
    }
}
