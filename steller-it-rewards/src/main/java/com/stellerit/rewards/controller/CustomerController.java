package com.stellerit.rewards.controller;

import com.stellerit.rewards.dto.CustomerRequest;
import com.stellerit.rewards.model.Customer;
import com.stellerit.rewards.service.CustomerService;
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
 * REST controller for customer management operations.
 */
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customers", description = "API for managing customers")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "Create a new customer", 
               description = "Creates a new customer in the rewards program")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Customer created successfully",
                    content = @Content(schema = @Schema(implementation = Customer.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "Customer with email already exists")
    })
    public ResponseEntity<Customer> createCustomer(
            @Valid @RequestBody CustomerRequest request) {
        log.info("POST /customers - Creating customer: {}", request.getEmail());
        Customer customer = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", 
               description = "Retrieves a customer by their unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer found",
                    content = @Content(schema = @Schema(implementation = Customer.class))),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Customer> getCustomerById(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable Long id) {
        log.info("GET /customers/{} - Fetching customer", id);
        Customer customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    @GetMapping
    @Operation(summary = "Get all customers", 
               description = "Retrieves all customers in the system")
    @ApiResponse(responseCode = "200", description = "List of customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        log.info("GET /customers - Fetching all customers");
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }
}

