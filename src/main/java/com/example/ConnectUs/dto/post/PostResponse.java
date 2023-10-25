package com.example.ConnectUs.dto.post;

import com.example.ConnectUs.dto.authentication.UserResponse;
import com.example.ConnectUs.dto.searchUsers.SearchUserResponse;
import com.example.ConnectUs.model.postgres.User;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse implements Comparable<PostResponse>{
    private Integer id;
    private Integer userId;
    private String firstname;
    private String lastname;
    private String profileImage;
    private String text;
    private List<String> images;
    private String dateAndTime;
    private boolean isLiked;
    private Integer numberOfLikes;
    private Integer numberOfComments;
    private List<SearchUserResponse> taggedUsers;

    @Override
    public int compareTo(PostResponse postResponse) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime1 = LocalDateTime.parse(getDateAndTime(), formatter);
        LocalDateTime dateTime2 = LocalDateTime.parse(postResponse.getDateAndTime(), formatter);

        return dateTime1.compareTo(dateTime2);
    }
}
