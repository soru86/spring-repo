package com.stellerit.rewards.service;

import com.stellerit.rewards.dto.RewardPointsResponse;
import com.stellerit.rewards.model.Customer;
import com.stellerit.rewards.model.Transaction;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RewardPointsService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Reward Points Service Tests")
class RewardPointsServiceTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private CustomerService customerService;

    @Mock
    private RewardPointsCalculator rewardPointsCalculator;

    @InjectMocks
    private RewardPointsService rewardPointsService;

    private Customer customer;
    private Transaction transaction1;
    private Transaction transaction2;
    private Transaction transaction3;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .createdAt(LocalDateTime.now())
                .build();

        LocalDateTime janDate = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime febDate = LocalDateTime.of(2024, 2, 15, 10, 0);
        LocalDateTime marDate = LocalDateTime.of(2024, 3, 15, 10, 0);

        transaction1 = Transaction.builder()
                .id(1L)
                .customer(customer)
                .amount(new BigDecimal("120.00"))
                .transactionDate(janDate)
                .build();

        transaction2 = Transaction.builder()
                .id(2L)
                .customer(customer)
                .amount(new BigDecimal("75.00"))
                .transactionDate(febDate)
                .build();

        transaction3 = Transaction.builder()
                .id(3L)
                .customer(customer)
                .amount(new BigDecimal("200.00"))
                .transactionDate(marDate)
                .build();
    }

    @Test
    @DisplayName("Should calculate reward points successfully")
    void testCalculateRewardPoints_Success() {
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2, transaction3);
        
        when(customerService.getCustomerById(1L)).thenReturn(customer);
        when(transactionService.getTransactionsByCustomerIdAndDateRange(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(transactions);
        when(rewardPointsCalculator.calculateRewardPoints(new BigDecimal("120.00")))
                .thenReturn(new BigDecimal("90.00"));
        when(rewardPointsCalculator.calculateRewardPoints(new BigDecimal("75.00")))
                .thenReturn(new BigDecimal("25.00"));
        when(rewardPointsCalculator.calculateRewardPoints(new BigDecimal("200.00")))
                .thenReturn(new BigDecimal("250.00"));

        RewardPointsResponse result = rewardPointsService.calculateRewardPoints(1L);

        assertNotNull(result);
        assertEquals(1L, result.getCustomerId());
        assertEquals("John Doe", result.getCustomerName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertNotNull(result.getMonthlyRewards());
        assertNotNull(result.getTotalRewardPoints());
        verify(customerService).getCustomerById(1L);
        verify(transactionService).getTransactionsByCustomerIdAndDateRange(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should return zero points when no transactions")
    void testCalculateRewardPoints_NoTransactions() {
        when(customerService.getCustomerById(1L)).thenReturn(customer);
        when(transactionService.getTransactionsByCustomerIdAndDateRange(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());

        RewardPointsResponse result = rewardPointsService.calculateRewardPoints(1L);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalRewardPoints());
        assertTrue(result.getMonthlyRewards().isEmpty());
    }

    @Test
    @DisplayName("Should group transactions by month correctly")
    void testCalculateRewardPoints_MultipleMonths() {
        LocalDateTime janDate1 = LocalDateTime.of(2024, 1, 5, 10, 0);
        LocalDateTime janDate2 = LocalDateTime.of(2024, 1, 20, 10, 0);
        LocalDateTime febDate = LocalDateTime.of(2024, 2, 15, 10, 0);

        Transaction janTxn1 = Transaction.builder()
                .id(1L)
                .customer(customer)
                .amount(new BigDecimal("100.00"))
                .transactionDate(janDate1)
                .build();

        Transaction janTxn2 = Transaction.builder()
                .id(2L)
                .customer(customer)
                .amount(new BigDecimal("50.00"))
                .transactionDate(janDate2)
                .build();

        Transaction febTxn = Transaction.builder()
                .id(3L)
                .customer(customer)
                .amount(new BigDecimal("150.00"))
                .transactionDate(febDate)
                .build();

        List<Transaction> transactions = Arrays.asList(janTxn1, janTxn2, febTxn);

        when(customerService.getCustomerById(1L)).thenReturn(customer);
        when(transactionService.getTransactionsByCustomerIdAndDateRange(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(transactions);
        when(rewardPointsCalculator.calculateRewardPoints(new BigDecimal("100.00")))
                .thenReturn(new BigDecimal("50.00"));
        when(rewardPointsCalculator.calculateRewardPoints(new BigDecimal("50.00")))
                .thenReturn(BigDecimal.ZERO);
        when(rewardPointsCalculator.calculateRewardPoints(new BigDecimal("150.00")))
                .thenReturn(new BigDecimal("150.00"));

        RewardPointsResponse result = rewardPointsService.calculateRewardPoints(1L);

        assertNotNull(result);
        assertEquals(2, result.getMonthlyRewards().size());
    }
}

