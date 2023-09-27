package com.example.ConnectUs.controller;

import com.example.ConnectUs.dto.searchUsers.SearchUserResponse;
import com.example.ConnectUs.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<SearchUserResponse>> searchUsers(@RequestParam String searchText, @RequestParam Integer userId) throws DataAccessException{
        try{
            return ResponseEntity.ok(userService.searchUsers(userId, searchText));
        }catch (DataAccessException e){
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }
}
