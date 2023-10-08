package com.example.ConnectUs.dto.pagePost;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagePostRequest {
    private String postText;
    private String imageInBase64;
    private Integer pageId;
}
