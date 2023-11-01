package com.example.ConnectUs.model.postgres;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private LocalDateTime dateAndTime;

    @ManyToOne
    @JoinColumn(name = "page_id")
    private Page page;

    @ManyToMany
    @JoinTable(
            name = "page_post_like",
            joinColumns = @JoinColumn(name = "page_post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonIgnore
    private List<User> likes;

    @OneToMany(mappedBy = "pagePost")
    private List<PagePostImage> images;
}
