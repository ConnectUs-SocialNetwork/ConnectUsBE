package com.example.ConnectUs.controller;

import com.example.ConnectUs.dto.post.LikeResponse;
import com.example.ConnectUs.dto.post.PostRequest;
import com.example.ConnectUs.dto.post.PostsResponse;
import com.example.ConnectUs.model.postgres.Post;
import com.example.ConnectUs.model.postgres.User;
import com.example.ConnectUs.service.PostService;
import com.example.ConnectUs.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.Base64;

@Controller
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final UserService userService;

    private final String fileDirectory = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "images";

    @PostMapping("/save")
    public ResponseEntity<Post> save(
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
}
