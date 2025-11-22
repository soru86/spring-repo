package com.sagademo.gateway.web;

import com.sagademo.common.web.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/fallback")
    public ResponseEntity<ApiResponse<Void>> fallback() {
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(false)
                        .message("Service temporarily unavailable, please retry.")
                        .build()
        );
    }
}

