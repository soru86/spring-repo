package com.sagademo.inventory.web;

import com.sagademo.common.dto.OrderRequest;
import com.sagademo.common.web.ApiResponse;
import com.sagademo.inventory.application.InventoryWorkflowService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryWorkflowService workflowService;

    public InventoryController(InventoryWorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping("/reserve")
    public ResponseEntity<ApiResponse<Void>> reserve(@RequestBody @Valid OrderRequest request) {
        boolean reserved = workflowService.reserve(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(reserved)
                .message(reserved ? "Inventory reserved" : "Insufficient stock")
                .build());
    }

    @PostMapping("/release")
    public ResponseEntity<ApiResponse<Void>> release(@RequestBody @Valid OrderRequest request) {
        workflowService.release(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Inventory released")
                .build());
    }
}

