package com.example.ConnectUs.service;

import com.example.ConnectUs.dto.comment.CommentRequest;
import com.example.ConnectUs.dto.comment.CommentResponse;
import com.example.ConnectUs.exceptions.DatabaseAccessException;
import com.example.ConnectUs.model.postgres.Comment;
import com.example.ConnectUs.model.postgres.Post;
import com.example.ConnectUs.model.postgres.User;
import com.example.ConnectUs.repository.postgres.CommentRepository;
import com.example.ConnectUs.repository.postgres.PostRepository;
import com.example.ConnectUs.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentResponse save(CommentRequest commentRequest){
        try{
            User user = userRepository.findById(commentRequest.getUserId()).orElseThrow();
            Post post = postRepository.findById(commentRequest.getPostId()).orElseThrow();

            Comment comment = Comment.builder()
                    .user(user)
                    .post(post)
                    .text(commentRequest.getText())
                    .build();

            commentRepository.save(comment);

            return CommentResponse.builder()
                    .id(comment.getId())
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .postId(post.getId())
                    .profilePicture(user.getProfileImage())
                    .text(comment.getText())
                    .userId(user.getId())
                    .build();
        }catch (DataAccessException e){
            throw new DatabaseAccessException(e.getMessage());
        }
    }
}
