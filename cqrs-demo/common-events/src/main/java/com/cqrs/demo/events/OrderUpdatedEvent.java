package com.cqrs.demo.events;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderUpdatedEvent extends BaseEvent {
    private UUID orderId;
    private String status;
    private BigDecimal totalAmount;

    public OrderUpdatedEvent() {
        super();
    }

    public OrderUpdatedEvent(UUID orderId, String status, BigDecimal totalAmount) {
        super();
        this.orderId = orderId;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}

