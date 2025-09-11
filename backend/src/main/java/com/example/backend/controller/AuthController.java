package com.example.backend.controller;

import com.example.backend.entity.User;
import com.example.backend.model.dto.LoginRequest;
import com.example.backend.model.dto.RegisterRequest;
import com.example.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest registerRequest) {
        User user = authService.register(registerRequest);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        // In a real application, you would authenticate the user here
        // For now, just return a success message as security is disabled
        return ResponseEntity.ok("Login successful for user: " + loginRequest.getUsername());
    }
}
