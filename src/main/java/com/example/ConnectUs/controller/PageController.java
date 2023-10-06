package com.example.ConnectUs.controller;

import com.example.ConnectUs.dto.page.PageRequest;
import com.example.ConnectUs.dto.page.PageResponse;
import com.example.ConnectUs.dto.page.ViewPageResponse;
import com.example.ConnectUs.exceptions.DatabaseAccessException;
import com.example.ConnectUs.model.postgres.Page;
import com.example.ConnectUs.service.PageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ResourceBundle;

@Controller
@RequestMapping("/api/v1/page")
@RequiredArgsConstructor
public class PageController {
    private final PageService pageService;

    @PostMapping
    public ResponseEntity<PageResponse> save(@RequestBody  PageRequest pageRequest){
        try{
            Page page = pageService.save(pageRequest);
            PageResponse pageResponse = PageResponse.builder()
                    .category(page.getCategory().toString())
                    .administratorId(page.getAdministrator().getId())
                    .name(page.getName())
                    .description(page.getDescription())
                    .build();
            return ResponseEntity.ok(pageResponse);
        }catch (DatabaseAccessException e){
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/getViewPageResponse")
    public ResponseEntity<ViewPageResponse> getViewPageResponse(@RequestParam("pageId") Integer pageId, @RequestParam("userId") Integer userId){
        try{
            return ResponseEntity.ok(pageService.getViewPageResponse(pageId, userId));
        }catch (DatabaseAccessException e){
            return ResponseEntity.status(500).body(null);
        }

    }}
