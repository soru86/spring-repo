package com.stellerit.rewards.service;

import com.stellerit.rewards.dto.CustomerRequest;
import com.stellerit.rewards.exception.DuplicateResourceException;
import com.stellerit.rewards.exception.ResourceNotFoundException;
import com.stellerit.rewards.model.Customer;
import com.stellerit.rewards.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for customer-related operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public Customer createCustomer(CustomerRequest request) {
        log.info("Creating customer with email: {}", request.getEmail());
        
        if (customerRepository.existsByEmail(request.getEmail())) {
            log.warn("Customer with email {} already exists", request.getEmail());
            throw new DuplicateResourceException(
                    String.format("Customer with email %s already exists", request.getEmail()));
        }

        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .build();

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created successfully with ID: {}", savedCustomer.getId());
        
        return savedCustomer;
    }

    @Transactional(readOnly = true)
    public Customer getCustomerById(Long id) {
        log.debug("Fetching customer with ID: {}", id);
        return customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Customer with ID {} not found", id);
                    return new ResourceNotFoundException("Customer", id);
                });
    }

    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        log.debug("Fetching all customers");
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Customer getCustomerByEmail(String email) {
        log.debug("Fetching customer with email: {}", email);
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Customer with email {} not found", email);
                    return new ResourceNotFoundException(
                            String.format("Customer with email %s not found", email));
                });
    }
}

