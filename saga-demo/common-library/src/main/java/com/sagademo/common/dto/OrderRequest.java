package com.sagademo.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
public class OrderRequest {
    @NotBlank
    String customerId;
    @NotNull
    BigDecimal totalAmount;
    @NotEmpty
    List<OrderLine> items;

    @Value
    @Builder
    public static class OrderLine {
        @NotBlank
        String sku;
        @NotNull
        Integer quantity;
        @NotNull
        BigDecimal price;
    }
}
