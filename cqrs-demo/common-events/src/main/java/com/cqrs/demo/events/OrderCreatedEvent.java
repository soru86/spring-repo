package com.cqrs.demo.events;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderCreatedEvent extends BaseEvent {
    private UUID orderId;
    private String customerId;
    private String productId;
    private Integer quantity;
    private BigDecimal totalAmount;
    private String status;

    public OrderCreatedEvent() {
        super();
    }

    public OrderCreatedEvent(UUID orderId, String customerId, String productId, 
                            Integer quantity, BigDecimal totalAmount, String status) {
        super();
        this.orderId = orderId;
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

