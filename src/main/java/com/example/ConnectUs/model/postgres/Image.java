package com.example.ConnectUs.model.postgres;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_post")
public class Image {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = "image_in_base64", columnDefinition = "TEXT")
    private String imageInBase64;

    @ManyToMany
    @JoinTable(
            name = "_image_user_tags",
            joinColumns = @JoinColumn(name = "image_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> taggedUsers;
}
