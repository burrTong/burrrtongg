package com.example.backend.controller;

import com.example.backend.config.TestConfig;
import com.example.backend.entity.Order;
import com.example.backend.model.OrderStatus;
import com.example.backend.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setTotalPrice(199.99);
        testOrder.setStatus(OrderStatus.PENDING);
    }

    @Test
    void getAllOrders_shouldReturnListOfOrders() throws Exception {
        Order order2 = new Order();
        order2.setId(2L);
        order2.setTotalPrice(99.99);
        order2.setStatus(OrderStatus.PROCESSING);

        List<Order> orders = Arrays.asList(testOrder, order2);
        when(orderService.getAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].status").value("PROCESSING"));
    }

    @Test
    void getOrderById_shouldReturnOrder() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(testOrder);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalPrice").value(199.99))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getOrdersByCustomer_shouldReturnOrders() throws Exception {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.getOrdersByCustomer(1L)).thenReturn(orders);

        mockMvc.perform(get("/api/orders/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void denyOrder_shouldReturnDeniedOrder() throws Exception {
        Order deniedOrder = new Order();
        deniedOrder.setId(1L);
        deniedOrder.setStatus(OrderStatus.CANCELED);
        
        when(orderService.denyOrder(1L)).thenReturn(deniedOrder);

        mockMvc.perform(put("/api/orders/1/deny"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELED"));
    }
}
