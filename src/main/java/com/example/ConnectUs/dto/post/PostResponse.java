package com.example.ConnectUs.dto.post;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private Integer id;
    private String text;
    private String imageInBase64;
    private LocalDateTime dateAndTime;
}
