package com.example.backend.controller;

import com.example.backend.entity.User;
import com.example.backend.model.dto.LoginRequest;
import com.example.backend.model.dto.RegisterRequest;
import com.example.backend.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") // Adjusted path to match proxy
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/customer")
    public ResponseEntity<User> registerCustomer(@RequestBody RegisterRequest registerRequest) {
        log.info("Registering new customer with username: {}", registerRequest.getUsername());
        User user = authService.registerCustomer(registerRequest);
        log.info("Customer registered successfully with id: {}", user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/register/admin")
    public ResponseEntity<User> registerAdmin(@RequestBody RegisterRequest registerRequest) {
        log.info("Registering new admin with username: {}", registerRequest.getUsername());
        User user = authService.registerAdmin(registerRequest);
        log.info("Admin registered successfully with id: {}", user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for username: {}", loginRequest.getUsername());
        User user = authService.login(loginRequest);
        log.info("User '{}' logged in successfully as {}", user.getUsername(), user.getRole());
        return ResponseEntity.ok(user);
    }
}
