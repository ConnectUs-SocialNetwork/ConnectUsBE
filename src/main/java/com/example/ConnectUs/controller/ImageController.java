package com.example.ConnectUs.controller;

import com.example.ConnectUs.dto.image.ImageResponse;
import com.example.ConnectUs.model.postgres.Image;
import com.example.ConnectUs.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/api/v1/image")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @GetMapping("/getUserImages")
    public ResponseEntity<List<ImageResponse>> getUserImages(@RequestParam Integer userId){
        try{
            return ResponseEntity.ok(imageService.getUserImages(userId));
        }catch (DataAccessException e){
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/getUserTaggedImages")
    public ResponseEntity<List<ImageResponse>> getUserTaggedImages(@RequestParam Integer userId){
        try{
            return ResponseEntity.ok(imageService.getUserTaggedImages(userId));
        }catch (DataAccessException e){
            return ResponseEntity.status(500).body(null);
        }
    }
}
