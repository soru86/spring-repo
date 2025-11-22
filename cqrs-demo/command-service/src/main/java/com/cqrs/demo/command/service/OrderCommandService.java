package com.cqrs.demo.command.service;

import com.cqrs.demo.command.dto.CreateOrderCommand;
import com.cqrs.demo.command.dto.UpdateOrderCommand;
import com.cqrs.demo.command.model.Order;
import com.cqrs.demo.command.repository.OrderRepository;
import com.cqrs.demo.events.OrderCreatedEvent;
import com.cqrs.demo.events.OrderUpdatedEvent;
import com.cqrs.demo.events.OrderCancelledEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrderCommandService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String ORDER_CREATED_TOPIC = "order-created";
    private static final String ORDER_UPDATED_TOPIC = "order-updated";
    private static final String ORDER_CANCELLED_TOPIC = "order-cancelled";

    public OrderCommandService(OrderRepository orderRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public Order createOrder(CreateOrderCommand command) {
        Order order = new Order();
        order.setCustomerId(command.getCustomerId());
        order.setProductId(command.getProductId());
        order.setQuantity(command.getQuantity());
        order.setTotalAmount(command.getTotalAmount());
        order.setStatus("PENDING");

        Order savedOrder = orderRepository.save(order);

        // Publish event to Kafka
        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getCustomerId(),
                savedOrder.getProductId(),
                savedOrder.getQuantity(),
                savedOrder.getTotalAmount(),
                savedOrder.getStatus()
        );
        kafkaTemplate.send(ORDER_CREATED_TOPIC, event);

        return savedOrder;
    }

    @Transactional
    public Order updateOrder(UUID orderId, UpdateOrderCommand command) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setStatus(command.getStatus());
        if (command.getTotalAmount() != null) {
            order.setTotalAmount(command.getTotalAmount());
        }

        Order updatedOrder = orderRepository.save(order);

        // Publish event to Kafka
        OrderUpdatedEvent event = new OrderUpdatedEvent(
                updatedOrder.getId(),
                updatedOrder.getStatus(),
                updatedOrder.getTotalAmount()
        );
        kafkaTemplate.send(ORDER_UPDATED_TOPIC, event);

        return updatedOrder;
    }

    @Transactional
    public void cancelOrder(UUID orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setStatus("CANCELLED");
        orderRepository.save(order);

        // Publish event to Kafka
        OrderCancelledEvent event = new OrderCancelledEvent(orderId, reason);
        kafkaTemplate.send(ORDER_CANCELLED_TOPIC, event);
    }
}

