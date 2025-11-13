package com.example.backend.controller;

import com.example.backend.config.TestConfig;
import com.example.backend.entity.User;
import com.example.backend.model.Role;
import com.example.backend.service.StockReportService;
import com.example.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private StockReportService stockReportService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("admin");
        testUser.setRole(Role.ADMIN);
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() throws Exception {
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("customer1");
        user2.setRole(Role.CUSTOMER);

        List<User> users = Arrays.asList(testUser, user2);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].username").value("customer1"));
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

    @Test
    void getWeeklyStockReport_shouldReturnStockReport() throws Exception {
        Map<String, Object> reportItem = new HashMap<>();
        reportItem.put("productName", "Test Product");
        reportItem.put("stock", 100);

        List<Map<String, Object>> report = Arrays.asList(reportItem);
        when(stockReportService.getWeeklyStockReport()).thenReturn(report);

        mockMvc.perform(get("/api/admin/stock-report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("Test Product"))
                .andExpect(jsonPath("$[0].stock").value(100));
    }
}
