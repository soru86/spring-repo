package com.stellerit.rewards.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Service for calculating reward points based on transaction amounts.
 * 
 * Reward Points Calculation Rules:
 * - For every dollar spent over $50: 1 point
 * - For every dollar spent over $100: additional 1 point
 * 
 * Example: $120 purchase
 * - Points from $50 threshold: (120 - 50) * 1 = 70 points
 * - Points from $100 threshold: (120 - 100) * 1 = 20 points
 * - Total: 70 + 20 = 90 points
 */
@Component
@Slf4j
public class RewardPointsCalculator {

    private static final BigDecimal FIFTY_DOLLARS = new BigDecimal("50.00");
    private static final BigDecimal ONE_HUNDRED_DOLLARS = new BigDecimal("100.00");
    private static final int SCALE = 2;

    /**
     * Calculates reward points for a given transaction amount.
     * 
     * @param amount the transaction amount
     * @return the calculated reward points
     */
    public BigDecimal calculateRewardPoints(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.debug("Invalid amount for reward calculation: {}", amount);
            return BigDecimal.ZERO;
        }

        BigDecimal rewardPoints = BigDecimal.ZERO;

        // Calculate points for amounts over $50
        if (amount.compareTo(FIFTY_DOLLARS) > 0) {
            BigDecimal overFifty = amount.subtract(FIFTY_DOLLARS);
            rewardPoints = rewardPoints.add(overFifty);
            log.debug("Points from $50 threshold: {} for amount: {}", overFifty, amount);
        }

        // Calculate additional points for amounts over $100
        if (amount.compareTo(ONE_HUNDRED_DOLLARS) > 0) {
            BigDecimal overHundred = amount.subtract(ONE_HUNDRED_DOLLARS);
            rewardPoints = rewardPoints.add(overHundred);
            log.debug("Points from $100 threshold: {} for amount: {}", overHundred, amount);
        }

        BigDecimal result = rewardPoints.setScale(SCALE, RoundingMode.HALF_UP);
        log.debug("Total reward points calculated: {} for amount: {}", result, amount);
        
        return result;
    }
}

