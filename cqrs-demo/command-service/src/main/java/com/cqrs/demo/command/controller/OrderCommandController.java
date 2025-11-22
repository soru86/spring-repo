package com.cqrs.demo.command.controller;

import com.cqrs.demo.command.dto.CreateOrderCommand;
import com.cqrs.demo.command.dto.UpdateOrderCommand;
import com.cqrs.demo.command.model.Order;
import com.cqrs.demo.command.service.OrderCommandService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderCommandController {

    private final OrderCommandService orderCommandService;

    public OrderCommandController(OrderCommandService orderCommandService) {
        this.orderCommandService = orderCommandService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody CreateOrderCommand command) {
        Order order = orderCommandService.createOrder(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderCommand command) {
        Order order = orderCommandService.updateOrder(id, command);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable UUID id,
            @RequestParam(required = false, defaultValue = "User cancellation") String reason) {
        orderCommandService.cancelOrder(id, reason);
        return ResponseEntity.noContent().build();
    }
}

