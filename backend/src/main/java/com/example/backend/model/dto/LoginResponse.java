package com.example.backend.model.dto;

import com.example.backend.entity.User;

public class LoginResponse {
    private String token;
    private Long id;
    private String username;
    private String role;

    public LoginResponse(String token, User user) {
        this.token = token;
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole().name();
    }

    // Getters
    public String getToken() {
        return token;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}
