package com.cqrs.demo.query.service;

import com.cqrs.demo.query.model.OrderView;
import com.cqrs.demo.query.repository.OrderViewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderQueryService {

    private final OrderViewRepository orderViewRepository;

    public OrderQueryService(OrderViewRepository orderViewRepository) {
        this.orderViewRepository = orderViewRepository;
    }

    public OrderView getOrderById(UUID id) {
        return orderViewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    public List<OrderView> getAllOrders() {
        return orderViewRepository.findAll();
    }

    public List<OrderView> getOrdersByCustomerId(String customerId) {
        return orderViewRepository.findByCustomerId(customerId);
    }

    public List<OrderView> getOrdersByStatus(String status) {
        return orderViewRepository.findByStatus(status);
    }
}

