package com.cqrs.demo.events;

import java.util.UUID;

public class OrderCancelledEvent extends BaseEvent {
    private UUID orderId;
    private String reason;

    public OrderCancelledEvent() {
        super();
    }

    public OrderCancelledEvent(UUID orderId, String reason) {
        super();
        this.orderId = orderId;
        this.reason = reason;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

