package com.example.ConnectUs.dto.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ImageResponse {
    private Integer id;
    private String image;
    private Integer postId;
    private Integer pagePostId;
}
