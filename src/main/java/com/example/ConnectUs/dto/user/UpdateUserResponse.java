package com.example.ConnectUs.dto.user;

import com.example.ConnectUs.dto.authentication.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserResponse {
    private UserResponse userResponse;
    private String message;
}
