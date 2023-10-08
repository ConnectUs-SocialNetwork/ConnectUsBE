package com.example.ConnectUs.controller;

import com.example.ConnectUs.dto.pagePost.PagePostRequest;
import com.example.ConnectUs.dto.pagePost.PagePostResponse;
import com.example.ConnectUs.dto.pagePost.PagePostsResponse;
import com.example.ConnectUs.dto.post.LikeResponse;
import com.example.ConnectUs.exceptions.DatabaseAccessException;
import com.example.ConnectUs.model.postgres.PagePost;
import com.example.ConnectUs.service.PagePostService;
import com.example.ConnectUs.service.PageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/page-post")
public class PagePostController {
    private final PagePostService pagePostService;
    private final PageService pageService;

    @PostMapping
    public ResponseEntity<PagePostResponse> save(@RequestBody PagePostRequest pagePostRequest){
        try{
            return ResponseEntity.ok(pagePostService.save(pagePostRequest));
        }catch (DatabaseAccessException e){
            return  ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/getPagePosts")
    public ResponseEntity<PagePostsResponse> getPagePosts(@RequestParam("pageId") Integer pageId, @RequestParam("myId") Integer myId){
        try{
            return ResponseEntity.ok(pagePostService.getPagePosts(pageId, myId));
        }catch (DatabaseAccessException e){
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/like")
    public ResponseEntity<LikeResponse> likePost(@RequestParam Integer userId, @RequestParam Integer postId){
        try{
            pagePostService.likePost(userId, postId);
            return ResponseEntity.ok(new LikeResponse("Successfully!"));
        }catch (DatabaseAccessException e){
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/unlike")
    public ResponseEntity<LikeResponse> unlikePost(@RequestParam Integer userId, @RequestParam Integer postId){
        try{
            pagePostService.unlikePost(userId, postId);
            return ResponseEntity.ok(new LikeResponse("Successfully!"));
        }catch (DatabaseAccessException e){
            return ResponseEntity.status(500).body(null);
        }
    }
}
