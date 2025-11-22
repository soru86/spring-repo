package com.cqrs.demo.query.consumer;

import com.cqrs.demo.events.OrderCreatedEvent;
import com.cqrs.demo.events.OrderUpdatedEvent;
import com.cqrs.demo.events.OrderCancelledEvent;
import com.cqrs.demo.query.model.OrderView;
import com.cqrs.demo.query.repository.OrderViewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OrderEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventConsumer.class);
    private final OrderViewRepository orderViewRepository;

    public OrderEventConsumer(OrderViewRepository orderViewRepository) {
        this.orderViewRepository = orderViewRepository;
    }

    @KafkaListener(topics = "order-created", groupId = "query-service-group")
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        logger.info("Received OrderCreatedEvent: {}", event.getOrderId());
        
        OrderView orderView = new OrderView();
        orderView.setId(event.getOrderId());
        orderView.setCustomerId(event.getCustomerId());
        orderView.setProductId(event.getProductId());
        orderView.setQuantity(event.getQuantity());
        orderView.setTotalAmount(event.getTotalAmount());
        orderView.setStatus(event.getStatus());
        orderView.setCreatedAt(LocalDateTime.now());
        orderView.setUpdatedAt(LocalDateTime.now());

        orderViewRepository.save(orderView);
        logger.info("Order view created: {}", event.getOrderId());
    }

    @KafkaListener(topics = "order-updated", groupId = "query-service-group")
    @Transactional
    public void handleOrderUpdated(OrderUpdatedEvent event) {
        logger.info("Received OrderUpdatedEvent: {}", event.getOrderId());
        
        OrderView orderView = orderViewRepository.findById(event.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order view not found: " + event.getOrderId()));
        
        orderView.setStatus(event.getStatus());
        if (event.getTotalAmount() != null) {
            orderView.setTotalAmount(event.getTotalAmount());
        }
        orderView.setUpdatedAt(LocalDateTime.now());

        orderViewRepository.save(orderView);
        logger.info("Order view updated: {}", event.getOrderId());
    }

    @KafkaListener(topics = "order-cancelled", groupId = "query-service-group")
    @Transactional
    public void handleOrderCancelled(OrderCancelledEvent event) {
        logger.info("Received OrderCancelledEvent: {}", event.getOrderId());
        
        OrderView orderView = orderViewRepository.findById(event.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order view not found: " + event.getOrderId()));
        
        orderView.setStatus("CANCELLED");
        orderView.setUpdatedAt(LocalDateTime.now());

        orderViewRepository.save(orderView);
        logger.info("Order view cancelled: {}", event.getOrderId());
    }
}

