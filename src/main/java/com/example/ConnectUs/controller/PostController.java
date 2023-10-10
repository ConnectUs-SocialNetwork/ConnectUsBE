package com.example.ConnectUs.controller;

import com.example.ConnectUs.dto.post.LikeResponse;
import com.example.ConnectUs.dto.post.PostRequest;
import com.example.ConnectUs.dto.post.PostResponse;
import com.example.ConnectUs.dto.post.PostsResponse;
import com.example.ConnectUs.dto.searchUsers.SearchUserResponse;
import com.example.ConnectUs.exceptions.DatabaseAccessException;
import com.example.ConnectUs.model.postgres.Post;
import com.example.ConnectUs.model.postgres.User;
import com.example.ConnectUs.service.PostService;
import com.example.ConnectUs.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final UserService userService;

    @PostMapping("/save")
    public ResponseEntity<PostResponse> save(
            @RequestBody PostRequest request
    ) {
        User user = userService.findByEmail(request.getUserEmail()).orElse(new User());
        Post post = Post.builder()
                .text(request.getPostText())
                .imageData(request.getImageInBase64())
                .dateAndTime(LocalDateTime.now())
                .user(user)
                .build();
        return ResponseEntity.ok(postService.save(post));
    }

    @GetMapping("/feed")
    public ResponseEntity<PostsResponse> getPostsForFeed(@RequestParam Integer userId){
        return ResponseEntity.ok(postService.getPostsForFeed(userId));
    }

    @PostMapping("/like")
    public ResponseEntity<LikeResponse> likePost(@RequestParam Integer userId, @RequestParam Integer postId){
        postService.likePost(userId, postId);
        return ResponseEntity.ok(new LikeResponse("Successfully!"));
    }

    @PostMapping("/unlike")
    public ResponseEntity<LikeResponse> unlikePost(@RequestParam Integer userId, @RequestParam Integer postId){
        postService.unlikePost(userId, postId);
        return ResponseEntity.ok(new LikeResponse("Successfully!"));
    }

    @GetMapping("/getUserPosts")
    public ResponseEntity<PostsResponse> getUserPosts(@RequestParam Integer userId, @RequestParam Integer myId){
        try{
            return ResponseEntity.ok(postService.getUserPosts(userId, myId));
        }catch (DatabaseAccessException e){
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/getUsersWhoLikedPost")
    public ResponseEntity<List<SearchUserResponse>> getUsersWhoLikedPost(@RequestParam Integer postId, @RequestParam Integer myId){
        try{
            return ResponseEntity.ok(postService.getUsersWhoLikedPost(postId, myId));
        }catch (DatabaseAccessException e){
            return ResponseEntity.status(500).body(null);
        }
    }
}
