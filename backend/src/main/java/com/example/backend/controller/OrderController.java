package com.example.backend.controller;

import com.example.backend.entity.Order;
import com.example.backend.model.OrderStatus;
import com.example.backend.model.dto.OrderRequest;
import com.example.backend.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders") // Add /api prefix
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Order createOrder(@RequestBody OrderRequest orderRequest) {
        log.info("Creating order for customer id: {} with {} items", 
                orderRequest.getCustomerId(), orderRequest.getItems().size());
        Order order = orderService.createOrder(orderRequest);
        log.info("Order created successfully with id: {}, total: ${}", order.getId(), order.getTotalPrice());
        return order;
    }

    @GetMapping
    public List<Order> getAllOrders() {
        log.info("Fetching all orders");
        List<Order> orders = orderService.getAllOrders();
        log.info("Retrieved {} orders", orders.size());
        return orders;
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        log.info("Fetching order with id: {}", id);
        return orderService.getOrderById(id);
    }

    @GetMapping("/customer/{customerId}")
    public List<Order> getOrdersByCustomer(@PathVariable Long customerId) {
        log.info("Fetching orders for customer id: {}", customerId);
        List<Order> orders = orderService.getOrdersByCustomer(customerId);
        log.info("Found {} orders for customer id: {}", orders.size(), customerId);
        return orders;
    }

    @PutMapping("/{id}/status")
    public Order updateOrderStatus(@PathVariable Long id, @RequestBody OrderStatus status) {
        log.info("Updating order id: {} status to: {}", id, status);
        Order order = orderService.updateOrderStatus(id, status);
        log.info("Order id: {} status updated successfully", id);
        return order;
    }

    @PutMapping("/{id}/deny")
    public Order denyOrder(@PathVariable Long id) {
        log.info("Denying order id: {}", id);
        Order order = orderService.denyOrder(id);
        log.info("Order id: {} denied successfully", id);
        return order;
    }
}
