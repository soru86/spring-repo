package com.cqrs.demo.query.repository;

import com.cqrs.demo.query.model.OrderView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderViewRepository extends JpaRepository<OrderView, UUID> {
    List<OrderView> findByCustomerId(String customerId);
    List<OrderView> findByStatus(String status);
}

