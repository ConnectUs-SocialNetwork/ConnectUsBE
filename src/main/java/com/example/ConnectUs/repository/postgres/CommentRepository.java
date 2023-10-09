package com.example.ConnectUs.repository.postgres;

import com.example.ConnectUs.model.postgres.Comment;
import com.example.ConnectUs.model.postgres.PagePostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Query("SELECT count(c) FROM Comment c WHERE c.post.id = :postId")
    int countAllCommentsByPostId(@Param("postId") Integer postId);
}
