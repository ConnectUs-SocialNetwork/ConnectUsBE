package com.example.ConnectUs.controller;

import com.example.ConnectUs.dto.image.ImageResponse;
import com.example.ConnectUs.service.ImageService;
import com.example.ConnectUs.service.PagePostImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/api/v1/page-post-image")
@RequiredArgsConstructor
public class PagePostImageController {
    private final PagePostImageService imageService;

    @GetMapping("/getPageImages")
    public ResponseEntity<List<ImageResponse>> getUserImages(@RequestParam Integer pageId){
        try{
            return ResponseEntity.ok(imageService.getPagePhotos(pageId));
        }catch (DataAccessException e){
            return ResponseEntity.status(500).body(null);
        }
    }
}
