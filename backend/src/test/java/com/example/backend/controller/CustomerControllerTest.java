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
    void getCustomerById_shouldReturnMessage() throws Exception {
        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Customer with ID: 1"));
    }

    @Test
    void createCustomer_shouldReturnMessage() throws Exception {
        mockMvc.perform(post("/customers"))
                .andExpect(status().isOk())
                .andExpect(content().string("New customer created"));
    }

    @Test
    void updateCustomer_shouldReturnMessage() throws Exception {
        mockMvc.perform(put("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Customer updated with ID: 1"));
    }

    @Test
    void deleteCustomer_shouldReturnMessage() throws Exception {
        mockMvc.perform(delete("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Customer deleted with ID: 1"));
    }
}
