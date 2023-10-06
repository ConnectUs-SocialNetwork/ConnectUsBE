package com.example.ConnectUs.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Integer id;
    private String text;
    private Integer userId;
    private String firstname;
    private String lastname;
    private String profilePicture;
    private Integer postId;
}
