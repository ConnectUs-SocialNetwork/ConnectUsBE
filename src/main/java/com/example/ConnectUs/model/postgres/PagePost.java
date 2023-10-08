package com.example.ConnectUs.model.postgres;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "page_post")
public class PagePost {
    @Id
    @GeneratedValue
    private Integer id;
    private String text;
    @Column(name = "imagedata", columnDefinition = "TEXT")
    private String imageData;
    private LocalDateTime dateAndTime;

    @ManyToOne
    @JoinColumn(name = "page_id")
    private Page page;

    @ManyToMany(mappedBy = "likedPagePosts")
    private List<User> likes;
}
