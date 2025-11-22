package com.cqrs.demo.command.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.UUID;

public class UpdateOrderCommand {
    @NotBlank(message = "Status is required")
    private String status;

    private BigDecimal totalAmount;

    public UpdateOrderCommand() {
    }

    public UpdateOrderCommand(String status, BigDecimal totalAmount) {
        this.status = status;
        this.totalAmount = totalAmount;
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

