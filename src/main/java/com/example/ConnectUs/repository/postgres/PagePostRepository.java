package com.example.ConnectUs.repository.postgres;

import com.example.ConnectUs.model.postgres.PagePost;
import com.example.ConnectUs.model.postgres.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PagePostRepository extends JpaRepository<PagePost, Integer> {
    @Query("SELECT p FROM PagePost p WHERE p.page.id = :pageId")
    List<PagePost> findAllByPageId(@Param("pageId") Integer pageId);
}
