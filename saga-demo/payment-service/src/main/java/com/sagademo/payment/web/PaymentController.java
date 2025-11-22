package com.sagademo.payment.web;

import com.sagademo.common.dto.OrderRequest;
import com.sagademo.common.web.ApiResponse;
import com.sagademo.payment.application.PaymentWorkflowService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentWorkflowService workflowService;

    public PaymentController(PaymentWorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping("/{orderId}/charge")
    public ResponseEntity<ApiResponse<Void>> charge(@PathVariable String orderId,
                                                    @RequestBody @Valid OrderRequest request) {
        boolean paid = workflowService.charge(orderId, request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(paid)
                .message("Payment captured")
                .build());
    }

    @PostMapping("/{orderId}/refund")
    public ResponseEntity<ApiResponse<Void>> refund(@PathVariable String orderId,
                                                    @RequestBody @Valid OrderRequest request) {
        workflowService.refund(orderId, request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Payment refunded")
                .build());
    }
}

