package com.cqrs.demo.query.controller;

import com.cqrs.demo.query.model.OrderView;
import com.cqrs.demo.query.service.OrderQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderQueryController {

    private final OrderQueryService orderQueryService;

    public OrderQueryController(OrderQueryService orderQueryService) {
        this.orderQueryService = orderQueryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderView> getOrderById(@PathVariable UUID id) {
        OrderView order = orderQueryService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<OrderView>> getAllOrders(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String status) {
        List<OrderView> orders;
        if (customerId != null) {
            orders = orderQueryService.getOrdersByCustomerId(customerId);
        } else if (status != null) {
            orders = orderQueryService.getOrdersByStatus(status);
        } else {
            orders = orderQueryService.getAllOrders();
        }
        return ResponseEntity.ok(orders);
    }
}

