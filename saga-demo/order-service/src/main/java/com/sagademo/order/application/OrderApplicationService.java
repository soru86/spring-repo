package com.sagademo.order.application;

import com.sagademo.common.dto.OrderRequest;
import com.sagademo.common.dto.OrderStatus;
import com.sagademo.common.messaging.SagaEvent;
import com.sagademo.order.domain.CustomerOrder;
import com.sagademo.order.domain.CustomerOrderRepository;
import com.sagademo.order.domain.OrderItem;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class OrderApplicationService {

    private final CustomerOrderRepository repository;
    private final KafkaTemplate<String, SagaEvent> kafkaTemplate;

    public OrderApplicationService(CustomerOrderRepository repository,
                                   KafkaTemplate<String, SagaEvent> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public CustomerOrder createOrder(OrderRequest request) {
        CustomerOrder order = new CustomerOrder();
        order.setCustomerId(request.getCustomerId());
        order.setTotalAmount(request.getTotalAmount());
        order.setStatus(OrderStatus.PENDING);
        order.setItems(request.getItems().stream().map(line -> {
            OrderItem item = new OrderItem();
            item.setSku(line.getSku());
            item.setPrice(line.getPrice());
            item.setQuantity(line.getQuantity());
            item.setOrder(order);
            return item;
        }).collect(Collectors.toList()));

        CustomerOrder saved = repository.save(order);
        kafkaTemplate.send("order-events", saved.getOrderId(), SagaEvent.builder()
                .orderId(saved.getOrderId())
                .payload(request)
                .status(OrderStatus.PENDING)
                .source("order-service")
                .message("Order created")
                .timestamp(saved.getCreatedAt())
                .build());
        return saved;
    }

    @Transactional
    public void updateStatus(String orderId, OrderStatus status, String message) {
        CustomerOrder order = repository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.setStatus(status);
        repository.save(order);
        kafkaTemplate.send("order-events", orderId, SagaEvent.builder()
                .orderId(orderId)
                .status(status)
                .source("order-service")
                .message(message)
                .payload(null)
                .timestamp(order.getCreatedAt())
                .build());
    }
}

