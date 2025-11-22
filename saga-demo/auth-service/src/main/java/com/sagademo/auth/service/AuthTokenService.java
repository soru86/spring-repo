package com.sagademo.auth.service;

import com.sagademo.auth.domain.UserAccount;
import com.sagademo.auth.domain.UserAccountRepository;
import com.sagademo.common.web.ApiResponse;
import com.sagademo.common.security.JwtTokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthTokenService {

    private final UserAccountRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthTokenService(UserAccountRepository repository,
                            PasswordEncoder passwordEncoder,
                            JwtTokenService jwtTokenService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    public ApiResponse<Map<String, String>> login(String username, String rawPassword) {
        UserAccount account = repository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(rawPassword, account.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        Map<String, Object> claims = Map.of("roles", account.getRoles());
        String token = jwtTokenService.generateToken(username, claims);
        return ApiResponse.<Map<String, String>>builder()
                .success(true)
                .message("Token issued")
                .data(Map.of("accessToken", token))
                .build();
    }
}

