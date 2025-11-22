package com.stellerit.rewards.exception;

import com.stellerit.rewards.dto.ApiErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GlobalExceptionHandler.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Global Exception Handler Tests")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/customers");
    }

    @Test
    @DisplayName("Should handle ResourceNotFoundException")
    void testHandleResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Customer", 1L);

        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler
                .handleResourceNotFoundException(ex, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Resource Not Found", response.getBody().getError());
    }

    @Test
    @DisplayName("Should handle DuplicateResourceException")
    void testHandleDuplicateResourceException() {
        DuplicateResourceException ex = new DuplicateResourceException("Email already exists");

        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler
                .handleDuplicateResourceException(ex, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Duplicate Resource", response.getBody().getError());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException")
    void testHandleMethodArgumentNotValidException() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("customerRequest", "email", "Email is required");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler
                .handleValidationExceptions(ex, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertNotNull(response.getBody().getDetails());
    }

    @Test
    @DisplayName("Should handle ConstraintViolationException")
    void testHandleConstraintViolationException() {
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        violations.add(violation);

        when(ex.getConstraintViolations()).thenReturn(violations);
        when(violation.getPropertyPath()).thenReturn(mock(jakarta.validation.Path.class));
        when(violation.getMessage()).thenReturn("Invalid value");

        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler
                .handleConstraintViolationException(ex, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    @DisplayName("Should handle generic Exception")
    void testHandleGenericException() {
        Exception ex = new Exception("Unexpected error");

        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler
                .handleGenericException(ex, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
    }
}

