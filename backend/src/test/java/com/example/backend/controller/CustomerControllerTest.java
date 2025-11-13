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
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllCustomers_shouldReturnMessage() throws Exception {
        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(content().string("List of all customers"));
    }

    @Test
    void createCustomer_shouldReturnMessage() throws Exception {
        mockMvc.perform(post("/customers"))
                .andExpect(status().isOk())
                .andExpect(content().string("New customer created"));
    }
}
