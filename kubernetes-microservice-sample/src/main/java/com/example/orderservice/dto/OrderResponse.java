package com.example.orderservice.dto;

import com.example.orderservice.model.OrderStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String customerName,
        String customerEmail,
        OffsetDateTime orderDate,
        BigDecimal totalAmount,
        OrderStatus status,
        List<OrderItemResponse> items) {
}
