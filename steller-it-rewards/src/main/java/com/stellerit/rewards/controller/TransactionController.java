package com.stellerit.rewards.controller;

import com.stellerit.rewards.dto.TransactionRequest;
import com.stellerit.rewards.model.Transaction;
import com.stellerit.rewards.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for transaction management operations.
 */
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transactions", description = "API for managing customer transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Create a new transaction", 
               description = "Creates a new transaction for a customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Transaction created successfully",
                    content = @Content(schema = @Schema(implementation = Transaction.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Transaction> createTransaction(
            @Valid @RequestBody TransactionRequest request) {
        log.info("POST /transactions - Creating transaction for customer ID: {}", 
                request.getCustomerId());
        Transaction transaction = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get transactions by customer ID", 
               description = "Retrieves all transactions for a specific customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of transactions"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<List<Transaction>> getTransactionsByCustomerId(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable Long customerId) {
        log.info("GET /transactions/customer/{} - Fetching transactions", customerId);
        List<Transaction> transactions = transactionService.getTransactionsByCustomerId(customerId);
        return ResponseEntity.ok(transactions);
    }
}

