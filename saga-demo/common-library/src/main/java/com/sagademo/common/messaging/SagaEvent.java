package com.sagademo.common.messaging;

import com.sagademo.common.dto.OrderRequest;
import com.sagademo.common.dto.OrderStatus;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class SagaEvent {
    String orderId;
    OrderRequest payload;
    OrderStatus status;
    String source;
    Instant timestamp;
    String message;
}

