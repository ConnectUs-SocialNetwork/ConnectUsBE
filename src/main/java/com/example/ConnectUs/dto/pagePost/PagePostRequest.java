package com.example.ConnectUs.dto.pagePost;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagePostRequest {
    private String postText;
    private List<String> images;
    private Integer pageId;
}
