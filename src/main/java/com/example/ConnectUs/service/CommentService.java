package com.example.ConnectUs.service;

import com.example.ConnectUs.dto.comment.CommentRequest;
import com.example.ConnectUs.dto.comment.CommentResponse;
import com.example.ConnectUs.enumerations.NotificationType;
import com.example.ConnectUs.exceptions.DatabaseAccessException;
import com.example.ConnectUs.model.postgres.*;
import com.example.ConnectUs.repository.postgres.CommentRepository;
import com.example.ConnectUs.repository.postgres.PostRepository;
import com.example.ConnectUs.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;

    public CommentResponse save(CommentRequest commentRequest) {
        try {
            User user = userRepository.findById(commentRequest.getUserId()).orElseThrow();
            Post post = postRepository.findById(commentRequest.getPostId()).orElseThrow();

            Comment comment = Comment.builder()
                    .user(user)
                    .post(post)
                    .text(commentRequest.getText())
                    .build();

            commentRepository.save(comment);

            if(post.getUser().getId() != user.getId()){
                notificationService.save(Notification.builder()
                        .firstname(user.getFirstname())
                        .lastname(user.getLastname())
                        .user(post.getUser())
                        .avatar(user.getProfileImage())
                        .type(NotificationType.COMMENT)
                        .dateAndTime(LocalDateTime.now())
                        .entityId(post.getId())
                        .isRead(false)
                        .text("comment your post. Click on the notification to see the post.")
                        .build());
            }

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

    public CommentResponse delete(Integer commentId){
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        commentRepository.delete(comment);
        return CommentResponse.builder()
                .id(comment.getId())
                .build();
    }

    public List<CommentResponse> getComments(Integer postId) {
        try {
            List<Comment> postComments = commentRepository.findAllByPagePostId(postId);
            List<CommentResponse> commentResponses = new ArrayList<>();

            for (Comment comment : postComments) {
                commentResponses.add(CommentResponse.builder()
                        .id(comment.getId())
                        .text(comment.getText())
                        .postId(comment.getPost().getId())
                        .profilePicture(comment.getUser().getProfileImage())
                        .firstname(comment.getUser().getFirstname())
                        .lastname(comment.getUser().getLastname())
                        .userId(comment.getUser().getId())
                        .build());
            }

            return commentResponses;
        } catch (DataAccessException e) {
            throw new DatabaseAccessException(e.getMessage());
        }
    }
}
