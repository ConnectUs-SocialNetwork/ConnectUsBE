package com.example.ConnectUs.controller;

import com.example.ConnectUs.dto.comment.CommentRequest;
import com.example.ConnectUs.dto.comment.CommentResponse;
import com.example.ConnectUs.exceptions.DatabaseAccessException;
import com.example.ConnectUs.service.PagePostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/page-post-comment")
public class PagePostCommentController {
    private final PagePostCommentService pagePostCommentService;

    @PostMapping
    public ResponseEntity<CommentResponse> save(@RequestBody CommentRequest commentRequest){
        try{
            return  ResponseEntity.ok(pagePostCommentService.save(commentRequest));
        }catch (DatabaseAccessException e){
            return ResponseEntity.status(500).body(null);
        }
    }
}
