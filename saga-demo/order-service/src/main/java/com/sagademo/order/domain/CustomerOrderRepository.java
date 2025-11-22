package com.sagademo.order.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
    Optional<CustomerOrder> findByOrderId(String orderId);
}

