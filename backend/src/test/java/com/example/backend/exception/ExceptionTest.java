package com.example.backend.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ExceptionTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void testResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");
        assertEquals("Resource not found", exception.getMessage());
        assertNotNull(exception);
    }

    @Test
    void testResourceNotFoundExceptionHandler() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Product not found");
        WebRequest request = mock(WebRequest.class);
        
        ResponseEntity<?> response = globalExceptionHandler.resourceNotFoundException(exception, request);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testUserAlreadyExistsException() {
        UserAlreadyExistsException exception = new UserAlreadyExistsException("User already exists");
        assertEquals("User already exists", exception.getMessage());
        assertNotNull(exception);
    }

    @Test
    void testUserAlreadyExistsExceptionHandler() {
        UserAlreadyExistsException exception = new UserAlreadyExistsException("test@example.com already exists");
        WebRequest request = mock(WebRequest.class);
        
        ResponseEntity<?> response = globalExceptionHandler.userAlreadyExistsException(exception, request);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testBadCredentialsExceptionHandler() {
        BadCredentialsException exception = new BadCredentialsException("Invalid username or password");
        WebRequest request = mock(WebRequest.class);
        
        ResponseEntity<?> response = globalExceptionHandler.badCredentialsException(exception, request);
        
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGlobalExceptionHandler() {
        Exception exception = new Exception("Unexpected error");
        WebRequest request = mock(WebRequest.class);
        
        ResponseEntity<?> response = globalExceptionHandler.globalExceptionHandler(exception, request);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGlobalExceptionHandlerWithRuntimeException() {
        RuntimeException exception = new RuntimeException("Runtime error occurred");
        WebRequest request = mock(WebRequest.class);
        
        ResponseEntity<?> response = globalExceptionHandler.globalExceptionHandler(exception, request);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGlobalExceptionHandlerWithNullPointerException() {
        NullPointerException exception = new NullPointerException("Null pointer error");
        WebRequest request = mock(WebRequest.class);
        
        ResponseEntity<?> response = globalExceptionHandler.globalExceptionHandler(exception, request);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
