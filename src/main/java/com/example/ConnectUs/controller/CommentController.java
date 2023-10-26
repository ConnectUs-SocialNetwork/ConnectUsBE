package com.example.ConnectUs.controller;

import com.example.ConnectUs.dto.comment.CommentRequest;
import com.example.ConnectUs.dto.comment.CommentResponse;
import com.example.ConnectUs.exceptions.DatabaseAccessException;
import com.example.ConnectUs.model.postgres.Comment;
import com.example.ConnectUs.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> save(@RequestBody CommentRequest commentRequest){
        try{
            return ResponseEntity.ok(commentService.save(commentRequest));
        }catch (DatabaseAccessException e){
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<CommentResponse> delete(@RequestParam Integer commentId){
        return ResponseEntity.ok(commentService.delete(commentId));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@RequestParam Integer postId){
        try{
            return ResponseEntity.ok(commentService.getComments(postId));
        }catch (DatabaseAccessException e){
            return ResponseEntity.status(500).body(null);
        }
    }
}
