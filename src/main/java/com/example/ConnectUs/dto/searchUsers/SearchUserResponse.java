package com.example.ConnectUs.dto.searchUsers;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchUserResponse {
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String profileImage;
    private boolean friend;
    private Integer numberOfFriends;
    private Integer numberOfMutualFriends;
    private String country;
    private String city;
    private String street;
    private String number;
}
