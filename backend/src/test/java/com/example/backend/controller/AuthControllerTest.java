package com.example.backend.controller;

import com.example.backend.entity.User;
import com.example.backend.model.Role;
import com.example.backend.model.dto.LoginRequest;
import com.example.backend.model.dto.RegisterRequest;
import com.example.backend.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.example.backend.exception.UserAlreadyExistsException;
import org.springframework.security.authentication.BadCredentialsException;
import com.example.backend.exception.GlobalExceptionHandler;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;

@WebMvcTest(
    controllers = AuthController.class,
    excludeAutoConfiguration = {
        ElasticsearchRestClientAutoConfiguration.class,
        ElasticsearchDataAutoConfiguration.class
    }
)
class AuthControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService))
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    void registerCustomer_shouldReturnCreatedUser() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("newcustomer@example.com");
        registerRequest.setPassword("password123");

        User registeredUser = new User();
        registeredUser.setId(1L);
        registeredUser.setUsername("newcustomer@example.com");
        registeredUser.setRole(Role.CUSTOMER);

        when(authService.registerCustomer(any(RegisterRequest.class))).thenReturn(registeredUser);

        mockMvc.perform(post("/api/auth/register/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("newcustomer@example.com"))
                .andExpect(jsonPath("$.role").value(Role.CUSTOMER.name()));
    }

    @Test
    void registerAdmin_shouldReturnCreatedUser() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("newadmin@example.com");
        registerRequest.setPassword("adminpassword");

        User registeredUser = new User();
        registeredUser.setId(2L);
        registeredUser.setUsername("newadmin@example.com");
        registeredUser.setRole(Role.ADMIN);

        when(authService.registerAdmin(any(RegisterRequest.class))).thenReturn(registeredUser);

        mockMvc.perform(post("/api/auth/register/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.username").value("newadmin@example.com"))
                .andExpect(jsonPath("$.role").value(Role.ADMIN.name()));
    }

    @Test
    void login_shouldReturnUser() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("customer@example.com");
        loginRequest.setPassword("password123");

        User loggedInUser = new User();
        loggedInUser.setId(1L);
        loggedInUser.setUsername("customer@example.com");
        loggedInUser.setRole(Role.CUSTOMER);

        when(authService.login(any(LoginRequest.class))).thenReturn(loggedInUser);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("customer@example.com"))
                .andExpect(jsonPath("$.role").value(Role.CUSTOMER.name()));
    }

    @Test
    void registerCustomer_shouldReturnConflict_whenUserExists() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("existingcustomer@example.com");
        registerRequest.setPassword("password123");

        when(authService.registerCustomer(any(RegisterRequest.class)))
            .thenThrow(new UserAlreadyExistsException("User with this username already exists"));

        mockMvc.perform(post("/api/auth/register/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void login_shouldReturnUnauthorized_whenInvalidCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("customer@example.com");
        loginRequest.setPassword("wrongpassword");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
