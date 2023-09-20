package com.example.ConnectUs.service;

import com.example.ConnectUs.model.postgres.User;
import com.example.ConnectUs.repository.postgres.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }
}
