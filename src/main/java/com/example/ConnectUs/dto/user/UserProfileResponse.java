package com.example.ConnectUs.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    Integer id;
    String firstname;
    String lastname;
    String profilePicture;
    int numberOfFriends;
    int numberOfMutualFriends;
    boolean friends;
    boolean requested;
}
