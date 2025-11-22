package com.stellerit.rewards.service;

import com.stellerit.rewards.dto.TransactionRequest;
import com.stellerit.rewards.exception.ResourceNotFoundException;
import com.stellerit.rewards.model.Customer;
import com.stellerit.rewards.model.Transaction;
import com.stellerit.rewards.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TransactionService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Transaction Service Tests")
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private TransactionService transactionService;

    private Customer customer;
    private TransactionRequest transactionRequest;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .createdAt(LocalDateTime.now())
                .build();

        transactionRequest = TransactionRequest.builder()
                .customerId(1L)
                .amount(new BigDecimal("120.00"))
                .description("Test transaction")
                .transactionDate(LocalDateTime.now())
                .build();

        transaction = Transaction.builder()
                .id(1L)
                .customer(customer)
                .amount(new BigDecimal("120.00"))
                .description("Test transaction")
                .transactionDate(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create transaction successfully")
    void testCreateTransaction_Success() {
        when(customerService.getCustomerById(1L)).thenReturn(customer);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction result = transactionService.createTransaction(transactionRequest);

        assertNotNull(result);
        assertEquals(transaction.getId(), result.getId());
        assertEquals(transaction.getAmount(), result.getAmount());
        verify(customerService).getCustomerById(1L);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should create transaction with current date when date not provided")
    void testCreateTransaction_WithoutDate() {
        transactionRequest.setTransactionDate(null);
        when(customerService.getCustomerById(1L)).thenReturn(customer);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction result = transactionService.createTransaction(transactionRequest);

        assertNotNull(result);
        verify(customerService).getCustomerById(1L);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when customer not found")
    void testCreateTransaction_CustomerNotFound() {
        when(customerService.getCustomerById(1L))
                .thenThrow(new ResourceNotFoundException("Customer", 1L));

        assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.createTransaction(transactionRequest);
        });

        verify(customerService).getCustomerById(1L);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should get transactions by customer ID successfully")
    void testGetTransactionsByCustomerId_Success() {
        Transaction transaction2 = Transaction.builder()
                .id(2L)
                .customer(customer)
                .amount(new BigDecimal("75.00"))
                .transactionDate(LocalDateTime.now())
                .build();

        List<Transaction> transactions = Arrays.asList(transaction, transaction2);
        when(customerService.getCustomerById(1L)).thenReturn(customer);
        when(transactionRepository.findByCustomerId(1L)).thenReturn(transactions);

        List<Transaction> result = transactionService.getTransactionsByCustomerId(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(customerService).getCustomerById(1L);
        verify(transactionRepository).findByCustomerId(1L);
    }

    @Test
    @DisplayName("Should throw exception when customer not found for transactions")
    void testGetTransactionsByCustomerId_CustomerNotFound() {
        when(customerService.getCustomerById(1L))
                .thenThrow(new ResourceNotFoundException("Customer", 1L));

        assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.getTransactionsByCustomerId(1L);
        });

        verify(customerService).getCustomerById(1L);
        verify(transactionRepository, never()).findByCustomerId(anyLong());
    }

    @Test
    @DisplayName("Should get transactions by customer ID and date range successfully")
    void testGetTransactionsByCustomerIdAndDateRange_Success() {
        LocalDateTime startDate = LocalDateTime.now().minusMonths(3);
        LocalDateTime endDate = LocalDateTime.now();

        List<Transaction> transactions = Arrays.asList(transaction);
        when(customerService.getCustomerById(1L)).thenReturn(customer);
        when(transactionRepository.findByCustomerIdAndDateRange(1L, startDate, endDate))
                .thenReturn(transactions);

        List<Transaction> result = transactionService
                .getTransactionsByCustomerIdAndDateRange(1L, startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(customerService).getCustomerById(1L);
        verify(transactionRepository).findByCustomerIdAndDateRange(1L, startDate, endDate);
    }
}

