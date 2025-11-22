package com.stellerit.rewards.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RewardPointsCalculator.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Reward Points Calculator Tests")
class RewardPointsCalculatorTest {

    @InjectMocks
    private RewardPointsCalculator rewardPointsCalculator;

    @BeforeEach
    void setUp() {
        rewardPointsCalculator = new RewardPointsCalculator();
    }

    @Test
    @DisplayName("Should return zero points for amount less than $50")
    void testCalculateRewardPoints_LessThanFifty() {
        BigDecimal amount = new BigDecimal("45.00");
        BigDecimal result = rewardPointsCalculator.calculateRewardPoints(amount);
        
        assertEquals(BigDecimal.ZERO.setScale(2), result);
    }

    @Test
    @DisplayName("Should return zero points for exactly $50")
    void testCalculateRewardPoints_ExactlyFifty() {
        BigDecimal amount = new BigDecimal("50.00");
        BigDecimal result = rewardPointsCalculator.calculateRewardPoints(amount);
        
        assertEquals(BigDecimal.ZERO.setScale(2), result);
    }

    @Test
    @DisplayName("Should return correct points for amount between $50 and $100")
    void testCalculateRewardPoints_BetweenFiftyAndHundred() {
        BigDecimal amount = new BigDecimal("75.00");
        BigDecimal expected = new BigDecimal("25.00");
        BigDecimal result = rewardPointsCalculator.calculateRewardPoints(amount);
        
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should return correct points for exactly $100")
    void testCalculateRewardPoints_ExactlyHundred() {
        BigDecimal amount = new BigDecimal("100.00");
        BigDecimal expected = new BigDecimal("50.00");
        BigDecimal result = rewardPointsCalculator.calculateRewardPoints(amount);
        
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should return correct points for amount over $100 (example: $120)")
    void testCalculateRewardPoints_OverHundred() {
        BigDecimal amount = new BigDecimal("120.00");
        // (120 - 50) + (120 - 100) = 70 + 20 = 90
        BigDecimal expected = new BigDecimal("90.00");
        BigDecimal result = rewardPointsCalculator.calculateRewardPoints(amount);
        
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should return correct points for large amount")
    void testCalculateRewardPoints_LargeAmount() {
        BigDecimal amount = new BigDecimal("200.00");
        // (200 - 50) + (200 - 100) = 150 + 100 = 250
        BigDecimal expected = new BigDecimal("250.00");
        BigDecimal result = rewardPointsCalculator.calculateRewardPoints(amount);
        
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should return zero for null amount")
    void testCalculateRewardPoints_NullAmount() {
        BigDecimal result = rewardPointsCalculator.calculateRewardPoints(null);
        
        assertEquals(BigDecimal.ZERO.setScale(2), result);
    }

    @Test
    @DisplayName("Should return zero for zero amount")
    void testCalculateRewardPoints_ZeroAmount() {
        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal result = rewardPointsCalculator.calculateRewardPoints(amount);
        
        assertEquals(BigDecimal.ZERO.setScale(2), result);
    }

    @Test
    @DisplayName("Should return zero for negative amount")
    void testCalculateRewardPoints_NegativeAmount() {
        BigDecimal amount = new BigDecimal("-10.00");
        BigDecimal result = rewardPointsCalculator.calculateRewardPoints(amount);
        
        assertEquals(BigDecimal.ZERO.setScale(2), result);
    }

    @Test
    @DisplayName("Should handle decimal amounts correctly")
    void testCalculateRewardPoints_DecimalAmount() {
        BigDecimal amount = new BigDecimal("125.75");
        // (125.75 - 50) + (125.75 - 100) = 75.75 + 25.75 = 101.50
        BigDecimal expected = new BigDecimal("101.50");
        BigDecimal result = rewardPointsCalculator.calculateRewardPoints(amount);
        
        assertEquals(expected, result);
    }
}

