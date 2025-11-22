package com.stellerit.rewards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stellerit.rewards.dto.TransactionRequest;
import com.stellerit.rewards.exception.ResourceNotFoundException;
import com.stellerit.rewards.model.Customer;
import com.stellerit.rewards.model.Transaction;
import com.stellerit.rewards.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for TransactionController.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Transaction Controller Tests")
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Transaction transaction;
    private TransactionRequest transactionRequest;
    private Customer customer;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
        objectMapper = new ObjectMapper();

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
    void testCreateTransaction_Success() throws Exception {
        when(transactionService.createTransaction(any(TransactionRequest.class)))
                .thenReturn(transaction);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.amount").value(120.00));

        verify(transactionService).createTransaction(any(TransactionRequest.class));
    }

    @Test
    @DisplayName("Should return bad request for invalid input")
    void testCreateTransaction_InvalidInput() throws Exception {
        TransactionRequest invalidRequest = TransactionRequest.builder()
                .customerId(null)
                .amount(new BigDecimal("-10.00"))
                .build();

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get transactions by customer ID successfully")
    void testGetTransactionsByCustomerId_Success() throws Exception {
        Transaction transaction2 = Transaction.builder()
                .id(2L)
                .customer(customer)
                .amount(new BigDecimal("75.00"))
                .transactionDate(LocalDateTime.now())
                .build();

        List<Transaction> transactions = Arrays.asList(transaction, transaction2);
        when(transactionService.getTransactionsByCustomerId(1L)).thenReturn(transactions);

        mockMvc.perform(get("/transactions/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(transactionService).getTransactionsByCustomerId(1L);
    }

    @Test
    @DisplayName("Should return not found when customer does not exist")
    void testGetTransactionsByCustomerId_CustomerNotFound() throws Exception {
        when(transactionService.getTransactionsByCustomerId(1L))
                .thenThrow(new ResourceNotFoundException("Customer", 1L));

        mockMvc.perform(get("/transactions/customer/1"))
                .andExpect(status().isNotFound());

        verify(transactionService).getTransactionsByCustomerId(1L);
    }
}

