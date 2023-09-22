package com.example.ConnectUs.repository.postgres;

import com.example.ConnectUs.model.postgres.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query("SELECT p FROM Post p WHERE p.user.id = :userId")
    List<Post> findAllByUserId(@Param("userId") Integer userId);

    Optional<Post> findById(Integer id);
}
