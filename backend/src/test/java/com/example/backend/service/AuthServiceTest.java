package com.example.backend.service;

import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.exception.UserAlreadyExistsException;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.CUSTOMER);
    }

    @Test
    void registerCustomer_shouldRegisterNewCustomer_whenUsernameNotExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser@example.com");
        request.setPassword("password123");

        when(userRepository.findByUsername("newuser@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = authService.registerCustomer(request);

        assertThat(result).isNotNull();
        verify(userRepository, times(1)).findByUsername("newuser@example.com");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerCustomer_shouldThrowException_whenUsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existing@example.com");
        request.setPassword("password123");

        when(userRepository.findByUsername("existing@example.com")).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> authService.registerCustomer(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("User with this username already exists");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerAdmin_shouldRegisterNewAdmin_whenUsernameNotExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("admin@example.com");
        request.setPassword("adminpass");

        User admin = new User();
        admin.setUsername("admin@example.com");
        admin.setRole(Role.ADMIN);

        when(userRepository.findByUsername("admin@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("adminpass")).thenReturn("encodedAdminPassword");
        when(userRepository.save(any(User.class))).thenReturn(admin);

        User result = authService.registerAdmin(request);

        assertThat(result).isNotNull();
        verify(passwordEncoder, times(1)).encode("adminpass");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerAdmin_shouldThrowException_whenUsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existing@example.com");

        when(userRepository.findByUsername("existing@example.com")).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> authService.registerAdmin(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("User with this username already exists");
    }

    @Test
    void login_shouldReturnUser_whenCredentialsAreValid() {
        LoginRequest request = new LoginRequest();
        request.setUsername("test@example.com");
        request.setPassword("password123");

        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        User result = authService.login(request);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findByUsername("test@example.com");
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
    }

    @Test
    void login_shouldThrowException_whenUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent@example.com");
        request.setPassword("password123");

        when(userRepository.findByUsername("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with username");

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_shouldThrowException_whenPasswordIsInvalid() {
        LoginRequest request = new LoginRequest();
        request.setUsername("test@example.com");
        request.setPassword("wrongpassword");

        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid password");

        verify(userRepository, times(1)).findByUsername("test@example.com");
        verify(passwordEncoder, times(1)).matches("wrongpassword", "encodedPassword");
    }
}
