package com.stellerit.rewards.repository;

import com.stellerit.rewards.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Transaction entity operations.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByCustomerId(Long customerId);
    
    @Query("SELECT t FROM Transaction t WHERE t.customer.id = :customerId " +
           "AND t.transactionDate >= :startDate AND t.transactionDate < :endDate")
    List<Transaction> findByCustomerIdAndDateRange(
            @Param("customerId") Long customerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}

