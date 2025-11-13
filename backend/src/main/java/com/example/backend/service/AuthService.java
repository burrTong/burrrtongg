package com.example.backend.service;

import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.exception.UserAlreadyExistsException;
import com.example.backend.model.Role;
import com.example.backend.model.dto.LoginRequest;
import com.example.backend.model.dto.RegisterRequest;
import com.example.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerCustomer(RegisterRequest registerRequest) {
        log.info("Attempting to register customer: {}", registerRequest.getUsername());
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            log.warn("Registration failed - username already exists: {}", registerRequest.getUsername());
            throw new UserAlreadyExistsException("User with this username already exists");
        }
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.CUSTOMER);
        User savedUser = userRepository.save(user);
        log.info("Customer registered successfully: {} (id: {})", savedUser.getUsername(), savedUser.getId());
        return savedUser;
    }

    public User registerAdmin(RegisterRequest registerRequest) {
        log.info("Attempting to register admin: {}", registerRequest.getUsername());
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            log.warn("Admin registration failed - username already exists: {}", registerRequest.getUsername());
            throw new UserAlreadyExistsException("User with this username already exists");
        }
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.ADMIN);
        User savedUser = userRepository.save(user);
        log.info("Admin registered successfully: {} (id: {})", savedUser.getUsername(), savedUser.getId());
        return savedUser;
    }

    public User login(LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login failed - user not found: {}", loginRequest.getUsername());
                    return new ResourceNotFoundException("User not found with username: " + loginRequest.getUsername());
                });

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.warn("Login failed - invalid password for user: {}", loginRequest.getUsername());
            throw new BadCredentialsException("Invalid password");
        }
        log.info("User logged in successfully: {} (role: {})", user.getUsername(), user.getRole());
        return user;
    }
}
