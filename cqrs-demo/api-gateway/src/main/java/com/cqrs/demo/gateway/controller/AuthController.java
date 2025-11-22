package com.cqrs.demo.gateway.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${jwt.secret:mySecretKeyForJWTTokenGenerationAndValidationShouldBeAtLeast256Bits}")
    private String jwtSecret;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        // Simple authentication - in production, use proper authentication service
        if (username != null && password != null) {
            String token = generateToken(username);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("username", username);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(401).build();
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> userData) {
        String username = userData.get("username");
        // In production, save user to database
        String token = generateToken(username);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("username", username);
        response.put("message", "User registered successfully");
        return ResponseEntity.ok(response);
    }

    private String generateToken(String username) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .claim("roles", "USER")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(24, ChronoUnit.HOURS)))
                .signWith(key)
                .compact();
    }
}

