package com.example.ConnectUs.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendedUserResponse {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String profileImage;
    private Integer numberOfFriends;
    private Integer numberOfMutualFriends;
    private String country;
    private String city;
    private String street;
    private String number;
    boolean requestSentByMe;
    boolean heSentFriendRequest;
    private Integer requestId;
}
