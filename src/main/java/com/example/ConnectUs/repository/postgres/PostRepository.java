package com.example.ConnectUs.repository.postgres;

import com.example.ConnectUs.model.postgres.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {
}
