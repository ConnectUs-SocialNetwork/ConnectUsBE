package com.example.ConnectUs.dto.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class PageResponse {
    private Integer id;
    private String name;
    private String description;
    private String category;
    private Integer administratorId;
}
