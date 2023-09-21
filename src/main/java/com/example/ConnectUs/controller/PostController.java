package com.example.ConnectUs.controller;

import ch.qos.logback.core.CoreConstants;
import com.example.ConnectUs.dto.authentication.AuthenticationResponse;
import com.example.ConnectUs.dto.authentication.RegisterRequest;
import com.example.ConnectUs.dto.post.GetPostsRequest;
import com.example.ConnectUs.dto.post.PostRequest;
import com.example.ConnectUs.dto.post.PostResponse;
import com.example.ConnectUs.dto.post.PostsResponse;
import com.example.ConnectUs.model.postgres.Post;
import com.example.ConnectUs.model.postgres.User;
import com.example.ConnectUs.service.AuthenticationService;
import com.example.ConnectUs.service.PostService;
import com.example.ConnectUs.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

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
        byte[] imageBytes = Base64.getDecoder().decode(request.getImageInBase64());
        User user = userService.findByEmail(request.getUserEmail()).orElse(new User());

        String fileName = postService.generateUniqueFileName(user);

        // Kreirajte putanju za čuvanje slike
        String imagePath = fileDirectory + File.separator + fileName;

        // Sačuvajte sliku na odgovarajućoj putanji
        try (FileOutputStream stream = new FileOutputStream(imagePath)) {
            stream.write(imageBytes);
        }catch (Exception e){
            return ResponseEntity.status(500).body(new Post());
        }
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
}
