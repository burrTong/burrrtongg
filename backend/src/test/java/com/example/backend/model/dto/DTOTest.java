package com.example.backend.model.dto;

import com.example.backend.model.Role;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DTOTest {

    @Test
    void productRequest_gettersAndSetters() {
        ProductRequest dto = new ProductRequest();
        dto.setName("Product");
        dto.setDescription("Description");
        dto.setPrice(100.0);
        dto.setStock(50);
        dto.setImageUrl("/image.png");
        dto.setSize("M");
        dto.setCategoryId(1L);

        assertEquals("Product", dto.getName());
        assertEquals("Description", dto.getDescription());
        assertEquals(100.0, dto.getPrice());
        assertEquals(50, dto.getStock());
        assertEquals("/image.png", dto.getImageUrl());
        assertEquals("M", dto.getSize());
        assertEquals(1L, dto.getCategoryId());
    }

    @Test
    void orderRequest_gettersAndSetters() {
        OrderRequest dto = new OrderRequest();
        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(1L);
        item.setQuantity(2);
        
        dto.setCustomerId(1L);
        dto.setItems(Arrays.asList(item));
        dto.setCouponCode("SAVE10");

        assertEquals(1L, dto.getCustomerId());
        assertEquals(1, dto.getItems().size());
        assertEquals("SAVE10", dto.getCouponCode());
    }

    @Test
    void orderItemRequest_gettersAndSetters() {
        OrderItemRequest dto = new OrderItemRequest();
        dto.setProductId(1L);
        dto.setQuantity(5);

        assertEquals(1L, dto.getProductId());
        assertEquals(5, dto.getQuantity());
    }

    @Test
    void loginRequest_gettersAndSetters() {
        LoginRequest dto = new LoginRequest();
        dto.setUsername("user@example.com");
        dto.setPassword("password123");

        assertEquals("user@example.com", dto.getUsername());
        assertEquals("password123", dto.getPassword());
    }

    @Test
    void registerRequest_gettersAndSetters() {
        RegisterRequest dto = new RegisterRequest();
        dto.setUsername("newuser@example.com");
        dto.setPassword("password123");

        assertEquals("newuser@example.com", dto.getUsername());
        assertEquals("password123", dto.getPassword());
    }

    @Test
    void loginResponse_gettersAndSetters() {
        LoginResponse dto = new LoginResponse("jwt-token-123", Role.ADMIN, 1L, "user@example.com");

        assertEquals("jwt-token-123", dto.getToken());
        assertEquals("user@example.com", dto.getUsername());
        assertEquals(Role.ADMIN, dto.getRole());
        assertEquals(1L, dto.getId());
        
        dto.setToken("new-token");
        assertEquals("new-token", dto.getToken());
    }

    @Test
    void productRequest_nullValues() {
        ProductRequest dto = new ProductRequest();
        
        assertNull(dto.getName());
        assertNull(dto.getDescription());
        assertNull(dto.getPrice());
        assertNull(dto.getStock());
        assertNull(dto.getImageUrl());
        assertNull(dto.getSize());
        assertNull(dto.getCategoryId());
    }

    @Test
    void orderRequest_nullValues() {
        OrderRequest dto = new OrderRequest();
        
        assertNull(dto.getCustomerId());
        assertNull(dto.getItems());
        assertNull(dto.getCouponCode());
    }

    @Test
    void loginRequest_nullValues() {
        LoginRequest dto = new LoginRequest();
        
        assertNull(dto.getUsername());
        assertNull(dto.getPassword());
    }
}
