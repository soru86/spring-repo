package com.stellerit.rewards.service;

import com.stellerit.rewards.dto.TransactionRequest;
import com.stellerit.rewards.exception.ResourceNotFoundException;
import com.stellerit.rewards.model.Customer;
import com.stellerit.rewards.model.Transaction;
import com.stellerit.rewards.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for transaction-related operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerService customerService;

    @Transactional
    public Transaction createTransaction(TransactionRequest request) {
        log.info("Creating transaction for customer ID: {} with amount: {}", 
                request.getCustomerId(), request.getAmount());
        
        Customer customer = customerService.getCustomerById(request.getCustomerId());
        
        Transaction transaction = Transaction.builder()
                .customer(customer)
                .amount(request.getAmount())
                .transactionDate(request.getTransactionDate() != null 
                        ? request.getTransactionDate() 
                        : LocalDateTime.now())
                .description(request.getDescription())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction created successfully with ID: {}", savedTransaction.getId());
        
        return savedTransaction;
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByCustomerId(Long customerId) {
        log.debug("Fetching transactions for customer ID: {}", customerId);
        customerService.getCustomerById(customerId); // Validate customer exists
        return transactionRepository.findByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByCustomerIdAndDateRange(
            Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Fetching transactions for customer ID: {} between {} and {}", 
                customerId, startDate, endDate);
        customerService.getCustomerById(customerId); // Validate customer exists
        return transactionRepository.findByCustomerIdAndDateRange(customerId, startDate, endDate);
    }
}

