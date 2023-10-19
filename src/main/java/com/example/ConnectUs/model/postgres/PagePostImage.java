package com.example.ConnectUs.model.postgres;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "page_post_image")
public class PagePostImage {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = "image", columnDefinition = "TEXT")
    private String image;

    @ManyToOne
    @JoinColumn(name = "page_post_id")
    private PagePost pagePost;
}
