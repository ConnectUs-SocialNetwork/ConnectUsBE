package com.example.ConnectUs.dto.friendRequest;

import com.example.ConnectUs.enumerations.FriendRequestStatus;
import com.example.ConnectUs.model.postgres.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ProcessFriendRequestResponse {
    private Integer id;
    private FriendRequestStatus status;
}
