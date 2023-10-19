package com.example.ConnectUs.repository.postgres;

import com.example.ConnectUs.model.postgres.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Integer> {
    @Query("SELECT i FROM Image i " +
            "INNER JOIN i.post p " +
            "WHERE p.user.id = :userId")
    List<Image> findImagesByUserId(@Param("userId") Integer userId);
}
