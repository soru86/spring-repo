package com.sagademo.auth.web;

import com.sagademo.auth.service.AuthTokenService;
import com.sagademo.auth.web.dto.LoginRequest;
import com.sagademo.common.web.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthTokenService authTokenService;

    public AuthController(AuthTokenService authTokenService) {
        this.authTokenService = authTokenService;
    }

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authTokenService.login(request.username(), request.password()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.<Void>builder().success(false).message(ex.getMessage()).build());
    }
}

