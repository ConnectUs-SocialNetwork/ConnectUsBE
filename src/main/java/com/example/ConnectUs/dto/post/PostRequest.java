package com.example.ConnectUs.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {
    private String postText;
    private String imageInBase64;
    private String userEmail;
}
