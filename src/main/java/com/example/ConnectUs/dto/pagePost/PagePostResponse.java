package com.example.ConnectUs.dto.pagePost;

import com.example.ConnectUs.dto.authentication.UserResponse;
import com.example.ConnectUs.dto.post.PostResponse;
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
public class PagePostResponse implements Comparable<PagePostResponse>{
    private Integer postId;
    private Integer pageId;
    private String name;
    private String profileImage;
    private String text;
    private String imageInBase64;
    private String dateAndTime;
    private boolean isLiked;
    private Integer numberOfLikes;
    private Integer numberOfComments;

    @Override
    public int compareTo(PagePostResponse postResponse) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime1 = LocalDateTime.parse(getDateAndTime(), formatter);
        LocalDateTime dateTime2 = LocalDateTime.parse(postResponse.getDateAndTime(), formatter);

        return dateTime1.compareTo(dateTime2);
    }
}
