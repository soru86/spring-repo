package com.stellerit.rewards.controller;

import com.stellerit.rewards.dto.RewardPointsResponse;
import com.stellerit.rewards.exception.ResourceNotFoundException;
import com.stellerit.rewards.service.RewardPointsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for RewardPointsController.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Reward Points Controller Tests")
class RewardPointsControllerTest {

    @Mock
    private RewardPointsService rewardPointsService;

    @InjectMocks
    private RewardPointsController rewardPointsController;

    private MockMvc mockMvc;
    private RewardPointsResponse rewardPointsResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(rewardPointsController).build();

        RewardPointsResponse.MonthlyRewardPoints janRewards = 
                RewardPointsResponse.MonthlyRewardPoints.builder()
                        .month("January")
                        .year(2024)
                        .rewardPoints(new BigDecimal("90.00"))
                        .transactionCount(5)
                        .build();

        RewardPointsResponse.MonthlyRewardPoints febRewards = 
                RewardPointsResponse.MonthlyRewardPoints.builder()
                        .month("February")
                        .year(2024)
                        .rewardPoints(new BigDecimal("75.00"))
                        .transactionCount(5)
                        .build();

        rewardPointsResponse = RewardPointsResponse.builder()
                .customerId(1L)
                .customerName("John Doe")
                .email("john.doe@example.com")
                .monthlyRewards(Arrays.asList(janRewards, febRewards))
                .totalRewardPoints(new BigDecimal("165.00"))
                .build();
    }

    @Test
    @DisplayName("Should calculate reward points successfully")
    void testCalculateRewardPoints_Success() throws Exception {
        when(rewardPointsService.calculateRewardPoints(1L)).thenReturn(rewardPointsResponse);

        mockMvc.perform(get("/rewards/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1L))
                .andExpect(jsonPath("$.customerName").value("John Doe"))
                .andExpect(jsonPath("$.totalRewardPoints").value(165.00))
                .andExpect(jsonPath("$.monthlyRewards.length()").value(2));

        verify(rewardPointsService).calculateRewardPoints(1L);
    }

    @Test
    @DisplayName("Should return not found when customer does not exist")
    void testCalculateRewardPoints_CustomerNotFound() throws Exception {
        when(rewardPointsService.calculateRewardPoints(1L))
                .thenThrow(new ResourceNotFoundException("Customer", 1L));

        mockMvc.perform(get("/rewards/customer/1"))
                .andExpect(status().isNotFound());

        verify(rewardPointsService).calculateRewardPoints(1L);
    }
}

