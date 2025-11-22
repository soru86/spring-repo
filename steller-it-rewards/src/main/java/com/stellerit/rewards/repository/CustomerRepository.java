package com.stellerit.rewards.repository;

import com.stellerit.rewards.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Customer entity operations.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    Optional<Customer> findByEmail(String email);
    
    boolean existsByEmail(String email);
}

