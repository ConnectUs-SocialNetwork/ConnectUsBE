package com.example.ConnectUs.repository.postgres;

import com.example.ConnectUs.model.postgres.PagePostComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagePostCommentRepository extends JpaRepository<PagePostComment, Integer> {
}
