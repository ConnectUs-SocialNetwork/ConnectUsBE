package com.example.ConnectUs.service;

import com.example.ConnectUs.dto.authentication.*;
import com.example.ConnectUs.enumerations.Gender;
import com.example.ConnectUs.enumerations.TokenType;
import com.example.ConnectUs.model.postgres.Token;
import com.example.ConnectUs.model.postgres.User;
import com.example.ConnectUs.repository.TokenRepository;
import com.example.ConnectUs.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .dateOfBirth(LocalDate.parse(request.getDateOfBirth()))
                .gender(Gender.valueOf(request.getGender()))
                .build();
        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .tokens(TokensResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).build())
                .user(UserResponse.builder().id(user.getId()).email(user.getEmail()).firstname(user.getFirstname()).lastname(user.getLastname()).dateOfBirth(user.getDateOfBirth().toString()).gender(user.getGender()).build())
                .message("Successfully!")
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        }catch(BadCredentialsException e){
            return AuthenticationResponse.builder()
                    .tokens(new TokensResponse())
                    .user(new UserResponse())
                    .message("Email or password are not correct!")
                    .build();

        }
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .tokens(TokensResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).build())
                .user(UserResponse.builder().id(user.getId()).email(user.getEmail()).firstname(user.getFirstname()).lastname(user.getLastname()).dateOfBirth(user.getDateOfBirth().toString()).gender(user.getGender()).build())
                .message("Successfully!")
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .tokens(TokensResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build())
                        .user(UserResponse.builder().id(user.getId()).email(user.getEmail()).firstname(user.getFirstname()).lastname(user.getLastname()).dateOfBirth(user.getDateOfBirth().toString()).gender(user.getGender()).build())
                        .message("Successfully!")
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}