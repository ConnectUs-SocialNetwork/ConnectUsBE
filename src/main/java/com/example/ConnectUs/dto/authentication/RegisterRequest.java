package com.example.ConnectUs.dto.authentication;

import com.example.ConnectUs.enumerations.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String dateOfBirth;
    private String gender;
    private String country;
    private String city;
    private String street;
    private String number;
}
