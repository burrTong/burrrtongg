package com.example.backend.controller;

import com.example.backend.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
class OrderItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllOrderItems_shouldReturnMessage() throws Exception {
        mockMvc.perform(get("/order-items"))
                .andExpect(status().isOk())
                .andExpect(content().string("List of all order items"));
    }

    @Test
    void createOrderItem_shouldReturnMessage() throws Exception {
        mockMvc.perform(post("/order-items"))
                .andExpect(status().isOk())
                .andExpect(content().string("New order item created"));
    }
}
