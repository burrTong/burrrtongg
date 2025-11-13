package com.example.backend.integration;

import com.example.backend.config.TestConfig;
import com.example.backend.entity.User;
import com.example.backend.model.Role;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
class AdminIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Create test users
        User admin = new User();
        admin.setUsername("admin" + System.currentTimeMillis());
        admin.setPassword("password");
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        User customer = new User();
        customer.setUsername("customer" + System.currentTimeMillis());
        customer.setPassword("password");
        customer.setRole(Role.CUSTOMER);
        userRepository.save(customer);
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    void getDashboard_shouldReturnDashboardData() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSellers").exists())
                .andExpect(jsonPath("$.totalCustomers").exists())
                .andExpect(jsonPath("$.totalSales").exists())
                .andExpect(jsonPath("$.bestSellingProduct").exists());
    }
}
