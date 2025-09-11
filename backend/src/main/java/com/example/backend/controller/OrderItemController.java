package com.example.backend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order-items")
public class OrderItemController {

    @GetMapping
    public String getAllOrderItems() {
        return "List of all order items";
    }

    @PostMapping
    public String createOrderItem() {
        return "New order item created";
    }
}

