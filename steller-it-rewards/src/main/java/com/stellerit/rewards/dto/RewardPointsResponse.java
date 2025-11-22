package com.stellerit.rewards.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO representing reward points summary for a customer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RewardPointsResponse {

    private Long customerId;
    private String customerName;
    private String email;
    
    @Builder.Default
    private List<MonthlyRewardPoints> monthlyRewards = List.of();
    
    private BigDecimal totalRewardPoints;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyRewardPoints {
        private String month;
        private Integer year;
        private BigDecimal rewardPoints;
        private Integer transactionCount;
    }
}

