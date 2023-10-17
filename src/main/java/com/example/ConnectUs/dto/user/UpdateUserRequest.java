package com.example.ConnectUs.dto.user;

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
public class UpdateUserRequest {
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String gender;
    private String dateOfBirth;
    private String profileImage;
}
