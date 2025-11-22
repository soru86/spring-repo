package com.stellerit.rewards.controller;

import com.stellerit.rewards.dto.RewardPointsResponse;
import com.stellerit.rewards.service.RewardPointsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for reward points calculation operations.
 */
@RestController
@RequestMapping("/rewards")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reward Points", description = "API for calculating and retrieving reward points")
public class RewardPointsController {

    private final RewardPointsService rewardPointsService;

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Calculate reward points for a customer", 
               description = "Calculates and returns reward points for a customer " +
                           "for the last 3 months, including monthly breakdown and total")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reward points calculated successfully",
                    content = @Content(schema = @Schema(implementation = RewardPointsResponse.class))),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<RewardPointsResponse> calculateRewardPoints(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable Long customerId) {
        log.info("GET /rewards/customer/{} - Calculating reward points", customerId);
        RewardPointsResponse response = rewardPointsService.calculateRewardPoints(customerId);
        return ResponseEntity.ok(response);
    }
}

