package com.stellerit.rewards.service;

import com.stellerit.rewards.dto.CustomerRequest;
import com.stellerit.rewards.exception.DuplicateResourceException;
import com.stellerit.rewards.exception.ResourceNotFoundException;
import com.stellerit.rewards.model.Customer;
import com.stellerit.rewards.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CustomerService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Customer Service Tests")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private CustomerRequest customerRequest;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customerRequest = CustomerRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        customer = Customer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create customer successfully")
    void testCreateCustomer_Success() {
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer result = customerService.createCustomer(customerRequest);

        assertNotNull(result);
        assertEquals(customer.getId(), result.getId());
        assertEquals(customer.getEmail(), result.getEmail());
        verify(customerRepository).existsByEmail(customerRequest.getEmail());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw exception when creating customer with duplicate email")
    void testCreateCustomer_DuplicateEmail() {
        when(customerRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            customerService.createCustomer(customerRequest);
        });

        verify(customerRepository).existsByEmail(customerRequest.getEmail());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should get customer by ID successfully")
    void testGetCustomerById_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Customer result = customerService.getCustomerById(1L);

        assertNotNull(result);
        assertEquals(customer.getId(), result.getId());
        verify(customerRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when customer not found")
    void testGetCustomerById_NotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            customerService.getCustomerById(1L);
        });

        verify(customerRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get all customers successfully")
    void testGetAllCustomers_Success() {
        Customer customer2 = Customer.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .createdAt(LocalDateTime.now())
                .build();

        List<Customer> customers = Arrays.asList(customer, customer2);
        when(customerRepository.findAll()).thenReturn(customers);

        List<Customer> result = customerService.getAllCustomers();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(customerRepository).findAll();
    }

    @Test
    @DisplayName("Should get customer by email successfully")
    void testGetCustomerByEmail_Success() {
        when(customerRepository.findByEmail("john.doe@example.com"))
                .thenReturn(Optional.of(customer));

        Customer result = customerService.getCustomerByEmail("john.doe@example.com");

        assertNotNull(result);
        assertEquals(customer.getEmail(), result.getEmail());
        verify(customerRepository).findByEmail("john.doe@example.com");
    }

    @Test
    @DisplayName("Should throw exception when customer email not found")
    void testGetCustomerByEmail_NotFound() {
        when(customerRepository.findByEmail("john.doe@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            customerService.getCustomerByEmail("john.doe@example.com");
        });

        verify(customerRepository).findByEmail("john.doe@example.com");
    }
}

