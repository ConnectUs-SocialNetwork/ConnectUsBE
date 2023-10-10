package com.example.ConnectUs.service;

import com.example.ConnectUs.dto.comment.CommentRequest;
import com.example.ConnectUs.dto.comment.CommentResponse;
import com.example.ConnectUs.exceptions.DatabaseAccessException;
import com.example.ConnectUs.model.postgres.*;
import com.example.ConnectUs.repository.postgres.PagePostCommentRepository;
import com.example.ConnectUs.repository.postgres.PagePostRepository;
import com.example.ConnectUs.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagePostCommentService {
    private final PagePostCommentRepository pagePostCommentRepository;
    private final PagePostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse save(CommentRequest commentRequest) {
        try {
            User user = userRepository.findById(commentRequest.getUserId()).orElseThrow();
            PagePost post = postRepository.findById(commentRequest.getPostId()).orElseThrow();

            PagePostComment comment = PagePostComment.builder()
                    .user(user)
                    .pagePost(post)
                    .text(commentRequest.getText())
                    .build();

            pagePostCommentRepository.save(comment);

            return CommentResponse.builder()
                    .id(comment.getId())
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .postId(post.getId())
                    .profilePicture(user.getProfileImage())
                    .text(comment.getText())
                    .userId(user.getId())
                    .build();
        } catch (DataAccessException e) {
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    public List<CommentResponse> getComments(Integer postId) {
        try {
            List<PagePostComment> pagePostComments = pagePostCommentRepository.findAllByPagePostId(postId);
            List<CommentResponse> commentResponses = new ArrayList<>();

            for (PagePostComment comment : pagePostComments) {
                commentResponses.add(CommentResponse.builder()
                        .id(comment.getId())
                        .text(comment.getText())
                        .postId(comment.getPagePost().getId())
                        .profilePicture(comment.getUser().getProfileImage())
                        .firstname(comment.getUser().getFirstname())
                        .lastname(comment.getUser().getLastname())
                        .userId(comment.getUser().getId())
                        .build());
            }

            return commentResponses;
        }catch (DataAccessException e){
            throw new DatabaseAccessException(e.getMessage());
        }
    }
}
