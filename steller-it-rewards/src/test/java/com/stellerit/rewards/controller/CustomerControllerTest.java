package com.stellerit.rewards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stellerit.rewards.dto.CustomerRequest;
import com.stellerit.rewards.exception.DuplicateResourceException;
import com.stellerit.rewards.exception.ResourceNotFoundException;
import com.stellerit.rewards.model.Customer;
import com.stellerit.rewards.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for CustomerController.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Customer Controller Tests")
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Customer customer;
    private CustomerRequest customerRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
        objectMapper = new ObjectMapper();

        customer = Customer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .createdAt(LocalDateTime.now())
                .build();

        customerRequest = CustomerRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
    }

    @Test
    @DisplayName("Should create customer successfully")
    void testCreateCustomer_Success() throws Exception {
        when(customerService.createCustomer(any(CustomerRequest.class))).thenReturn(customer);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(customerService).createCustomer(any(CustomerRequest.class));
    }

    @Test
    @DisplayName("Should return bad request for invalid input")
    void testCreateCustomer_InvalidInput() throws Exception {
        CustomerRequest invalidRequest = CustomerRequest.builder()
                .firstName("")
                .email("invalid-email")
                .build();

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get customer by ID successfully")
    void testGetCustomerById_Success() throws Exception {
        when(customerService.getCustomerById(1L)).thenReturn(customer);

        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(customerService).getCustomerById(1L);
    }

    @Test
    @DisplayName("Should return not found when customer does not exist")
    void testGetCustomerById_NotFound() throws Exception {
        when(customerService.getCustomerById(1L))
                .thenThrow(new ResourceNotFoundException("Customer", 1L));

        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isNotFound());

        verify(customerService).getCustomerById(1L);
    }

    @Test
    @DisplayName("Should get all customers successfully")
    void testGetAllCustomers_Success() throws Exception {
        Customer customer2 = Customer.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .createdAt(LocalDateTime.now())
                .build();

        List<Customer> customers = Arrays.asList(customer, customer2);
        when(customerService.getAllCustomers()).thenReturn(customers);

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(customerService).getAllCustomers();
    }
}

