package com.example.ConnectUs.controller;

import com.example.ConnectUs.dto.authentication.UserResponse;
import com.example.ConnectUs.dto.page.*;
import com.example.ConnectUs.dto.searchUsers.SearchUserResponse;
import com.example.ConnectUs.exceptions.DatabaseAccessException;
import com.example.ConnectUs.model.postgres.Page;
import com.example.ConnectUs.service.PageService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
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

    @PostMapping("/saveAll")
    public ResponseEntity<List<PageResponse>> saveAll(@RequestBody  List<PageRequest> pageRequestList){
        try{
            List<PageResponse> retList = new ArrayList<>();
            for(PageRequest pageRequest : pageRequestList){
                Page page = pageService.save(pageRequest);
                PageResponse pageResponse = PageResponse.builder()
                        .category(page.getCategory().toString())
                        .administratorId(page.getAdministrator().getId())
                        .name(page.getName())
                        .description(page.getDescription())
                        .build();
                retList.add(pageResponse);
            }
            return ResponseEntity.ok(retList);
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

    }

    @PutMapping("/like")
    public ResponseEntity likePage(@RequestParam("pageId") Integer pageId, @RequestParam("userId") Integer userId){
        try{
            pageService.likePage(pageId, userId);
            return ResponseEntity.ok(true);
        }catch (DatabaseAccessException e){
            return  ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping("/unlike")
    public ResponseEntity unlikePage(@RequestParam("pageId") Integer pageId, @RequestParam("userId") Integer userId){
        try{
            pageService.unlikePage(pageId, userId);
            return ResponseEntity.ok(true);
        }catch (DatabaseAccessException e){
            return  ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/getLikers")
    public ResponseEntity<List<SearchUserResponse>> getLikers(@RequestParam Integer pageId, @RequestParam Integer userId){
        try{
            return ResponseEntity.ok(pageService.getLikers(pageId, userId));
        }catch (DatabaseAccessException e){
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<SearchPageResponse>> searchPages(@RequestParam String searchText, @RequestParam Integer userId){
        try{
            return ResponseEntity.ok(pageService.searchPages(searchText, userId));
        }catch (DatabaseAccessException e){
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<PageResponse> updatePage(@RequestBody UpdatePageRequest updatePageRequest){
        try{
            return ResponseEntity.ok(pageService.updatePage(updatePageRequest));
        }catch (DataAccessException e){
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/getPage")
    public ResponseEntity<PageResponse> getPage(@RequestParam Integer pageId){
        try{
            return ResponseEntity.ok(pageService.getPage(pageId));
        }catch (DataAccessException e){
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/getRecommendedPages")
    public ResponseEntity<List<RecommendedPageResponse>> getRecommendedPages(@RequestParam Integer userId){
        return ResponseEntity.ok(pageService.recommendPagesThatHaveBeenLikedByMyFriends(userId.longValue()));
    }
}
