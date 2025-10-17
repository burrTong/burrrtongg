package com.example.backend.service;

import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.Role;
import com.example.backend.model.dto.LoginRequest;
import com.example.backend.model.dto.RegisterRequest;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequestCustomer;
    private RegisterRequest registerRequestAdmin;
    private LoginRequest loginRequest;
    private User customerUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        registerRequestCustomer = new RegisterRequest();
        registerRequestCustomer.setUsername("customer@example.com");
        registerRequestCustomer.setPassword("password123");

        registerRequestAdmin = new RegisterRequest();
        registerRequestAdmin.setUsername("admin@example.com");
        registerRequestAdmin.setPassword("adminpass");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("customer@example.com");
        loginRequest.setPassword("password123");

        customerUser = new User();
        customerUser.setId(1L);
        customerUser.setUsername("customer@example.com");
        customerUser.setPassword("encodedPassword");
        customerUser.setRole(Role.CUSTOMER);

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("admin@example.com");
        adminUser.setPassword("encodedAdminPass");
        adminUser.setRole(Role.ADMIN);
    }

    @Test
    void registerCustomer_shouldCreateNewCustomerUser_whenUserDoesNotExist() {
        when(userRepository.findByUsername(registerRequestCustomer.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerRequestCustomer.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        User registeredUser = authService.registerCustomer(registerRequestCustomer);

        assertNotNull(registeredUser);
        assertEquals(registerRequestCustomer.getUsername(), registeredUser.getUsername());
        assertEquals("encodedPassword", registeredUser.getPassword());
        assertEquals(Role.CUSTOMER, registeredUser.getRole());
        verify(userRepository, times(1)).findByUsername(registerRequestCustomer.getUsername());
        verify(passwordEncoder, times(1)).encode(registerRequestCustomer.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerCustomer_shouldThrowRuntimeException_whenUserAlreadyExists() {
        when(userRepository.findByUsername(registerRequestCustomer.getUsername())).thenReturn(Optional.of(customerUser));

        assertThrows(RuntimeException.class, () -> authService.registerCustomer(registerRequestCustomer));
        verify(userRepository, times(1)).findByUsername(registerRequestCustomer.getUsername());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerAdmin_shouldCreateNewAdminUser_whenUserDoesNotExist() {
        when(userRepository.findByUsername(registerRequestAdmin.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerRequestAdmin.getPassword())).thenReturn("encodedAdminPass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(2L);
            return savedUser;
        });

        User registeredUser = authService.registerAdmin(registerRequestAdmin);

        assertNotNull(registeredUser);
        assertEquals(registerRequestAdmin.getUsername(), registeredUser.getUsername());
        assertEquals("encodedAdminPass", registeredUser.getPassword());
        assertEquals(Role.ADMIN, registeredUser.getRole());
        verify(userRepository, times(1)).findByUsername(registerRequestAdmin.getUsername());
        verify(passwordEncoder, times(1)).encode(registerRequestAdmin.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void login_shouldReturnUser_whenCredentialsAreValid() {
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(customerUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), customerUser.getPassword())).thenReturn(true);

        User loggedInUser = authService.login(loginRequest);

        assertNotNull(loggedInUser);
        assertEquals(customerUser.getUsername(), loggedInUser.getUsername());
        verify(userRepository, times(1)).findByUsername(loginRequest.getUsername());
        verify(passwordEncoder, times(1)).matches(loginRequest.getPassword(), customerUser.getPassword());
    }

    @Test
    void login_shouldThrowResourceNotFoundException_whenUserNotFound() {
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.login(loginRequest));
        verify(userRepository, times(1)).findByUsername(loginRequest.getUsername());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_shouldThrowRuntimeException_whenInvalidPassword() {
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(customerUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), customerUser.getPassword())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
        verify(userRepository, times(1)).findByUsername(loginRequest.getUsername());
        verify(passwordEncoder, times(1)).matches(loginRequest.getPassword(), customerUser.getPassword());
    }
}
