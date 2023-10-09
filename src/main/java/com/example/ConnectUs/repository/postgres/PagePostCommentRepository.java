package com.example.ConnectUs.repository.postgres;

import com.example.ConnectUs.model.postgres.PagePost;
import com.example.ConnectUs.model.postgres.PagePostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PagePostCommentRepository extends JpaRepository<PagePostComment, Integer> {
    @Query("SELECT p FROM PagePostComment p WHERE p.pagePost.id = :pagePostId")
    List<PagePostComment> findAllByPagePostId(@Param("pagePostId") Integer pagePostId);

    @Query("SELECT count(p) FROM PagePostComment p WHERE p.pagePost.id = :pagePostId")
    int countAllCommentsByPostId(@Param("pagePostId") Integer pagePostId);
}
