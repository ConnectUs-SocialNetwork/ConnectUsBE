package com.example.ConnectUs.dto.authentication;

import com.example.ConnectUs.enumerations.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private Gender gender;
    private String dateOfBirth;
    private String profileImage;
}
