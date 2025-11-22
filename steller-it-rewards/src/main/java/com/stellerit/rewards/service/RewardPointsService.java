package com.stellerit.rewards.service;

import com.stellerit.rewards.dto.RewardPointsResponse;
import com.stellerit.rewards.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for calculating and retrieving reward points.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RewardPointsService {

    private final TransactionService transactionService;
    private final CustomerService customerService;
    private final RewardPointsCalculator rewardPointsCalculator;

    /**
     * Calculates reward points for a customer for the last 3 months.
     * 
     * @param customerId the customer ID
     * @return reward points summary
     */
    @Transactional(readOnly = true)
    public RewardPointsResponse calculateRewardPoints(Long customerId) {
        log.info("Calculating reward points for customer ID: {}", customerId);
        
        var customer = customerService.getCustomerById(customerId);
        
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(3);
        
        List<Transaction> transactions = transactionService
                .getTransactionsByCustomerIdAndDateRange(customerId, startDate, endDate);
        
        log.debug("Found {} transactions for customer ID: {}", transactions.size(), customerId);
        
        // Group transactions by month
        Map<Month, List<Transaction>> transactionsByMonth = transactions.stream()
                .collect(Collectors.groupingBy(t -> t.getTransactionDate().getMonth()));
        
        List<RewardPointsResponse.MonthlyRewardPoints> monthlyRewards = new ArrayList<>();
        BigDecimal totalRewardPoints = BigDecimal.ZERO;
        
        // Calculate points for each month
        for (Map.Entry<Month, List<Transaction>> entry : transactionsByMonth.entrySet()) {
            Month month = entry.getKey();
            List<Transaction> monthTransactions = entry.getValue();
            
            BigDecimal monthPoints = monthTransactions.stream()
                    .map(t -> rewardPointsCalculator.calculateRewardPoints(t.getAmount()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            totalRewardPoints = totalRewardPoints.add(monthPoints);
            
            RewardPointsResponse.MonthlyRewardPoints monthlyReward = 
                    RewardPointsResponse.MonthlyRewardPoints.builder()
                            .month(month.getDisplayName(TextStyle.FULL, Locale.ENGLISH))
                            .year(monthTransactions.get(0).getTransactionDate().getYear())
                            .rewardPoints(monthPoints)
                            .transactionCount(monthTransactions.size())
                            .build();
            
            monthlyRewards.add(monthlyReward);
            
            log.debug("Month: {}, Points: {}, Transactions: {}", 
                    month, monthPoints, monthTransactions.size());
        }
        
        RewardPointsResponse response = RewardPointsResponse.builder()
                .customerId(customer.getId())
                .customerName(customer.getFirstName() + " " + customer.getLastName())
                .email(customer.getEmail())
                .monthlyRewards(monthlyRewards)
                .totalRewardPoints(totalRewardPoints)
                .build();
        
        log.info("Total reward points calculated for customer ID {}: {}", 
                customerId, totalRewardPoints);
        
        return response;
    }
}

