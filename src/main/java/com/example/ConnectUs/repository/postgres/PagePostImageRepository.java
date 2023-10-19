package com.example.ConnectUs.repository.postgres;

import com.example.ConnectUs.model.postgres.Image;
import com.example.ConnectUs.model.postgres.PagePostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PagePostImageRepository extends JpaRepository<PagePostImage, Integer> {
    @Query("SELECT i FROM PagePostImage i " +
            "INNER JOIN i.pagePost p " +
            "WHERE p.page.id = :pageId")
    List<PagePostImage> findImagesByPageId(@Param("pageId") Integer pageId);
}
