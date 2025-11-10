package com.example.orderservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID id,
        String sku,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineAmount) {
}
