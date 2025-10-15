package com.example.backend.config;

import com.example.backend.repository.UserRepository;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    private final UserRepository userRepository;

    public ApplicationConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // The UserDetailsService and PasswordEncoder beans that were previously
    // defined in this file have been removed.
    // The UserDetailsService is now provided by the UserService class (annotated with @Service).
    // The PasswordEncoder is now defined in SecurityConfig.java.
    // This resolves the BeanDefinitionOverrideException.
}