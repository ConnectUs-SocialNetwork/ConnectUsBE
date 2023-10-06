package com.example.ConnectUs.dto.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ViewPageResponse {
    private Integer pageId;
    private String name;
    private String description;
    private String category;
    private String avatar;
    private int numberOfLikes;
    private boolean liked;
}
