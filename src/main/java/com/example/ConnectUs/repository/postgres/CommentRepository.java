package com.example.ConnectUs.repository.postgres;

import com.example.ConnectUs.model.postgres.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
