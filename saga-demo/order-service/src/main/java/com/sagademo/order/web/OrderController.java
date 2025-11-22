package com.sagademo.order.web;

import com.sagademo.common.dto.OrderRequest;
import com.sagademo.common.dto.OrderStatus;
import com.sagademo.common.web.ApiResponse;
import com.sagademo.order.application.OrderApplicationService;
import com.sagademo.order.domain.CustomerOrder;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderApplicationService service;

    public OrderController(OrderApplicationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerOrder>> create(@RequestBody @Valid OrderRequest request) {
        CustomerOrder order = service.createOrder(request);
        return ResponseEntity.ok(ApiResponse.<CustomerOrder>builder()
                .success(true)
                .message("Order accepted")
                .data(order)
                .build());
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<Void>> updateStatus(@PathVariable String orderId,
                                                          @RequestParam OrderStatus status,
                                                          @RequestParam(required = false) String message) {
        service.updateStatus(orderId, status, message != null ? message : "Status updated");
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Status updated")
                .build());
    }
}

