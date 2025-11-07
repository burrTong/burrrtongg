package com.example.backend.config;

import com.example.backend.repository.UserRepository;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    private final UserRepository userRepository;

    public ApplicationConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}