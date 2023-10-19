package com.example.ConnectUs.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {
    private String postText;
    private List<String> images;
    private String userEmail;
}
