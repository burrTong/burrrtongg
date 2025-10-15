package com.example.backend.service;

import com.example.backend.entity.OrderItem;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    public OrderItem getOrderItemById(Long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem not found with id " + id));
    }

    public OrderItem createOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    public OrderItem updateOrderItem(Long id, OrderItem orderItemDetails) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem not found with id " + id));

        // Assuming OrderItem has setters for its properties
        // You might want to update specific fields based on your entity structure
        orderItem.setProduct(orderItemDetails.getProduct()); // Example, adjust based on actual fields
        orderItem.setQuantity(orderItemDetails.getQuantity()); // Example
        orderItem.setPrice(orderItemDetails.getPrice()); // Example
        // ... update other relevant fields

        return orderItemRepository.save(orderItem);
    }

    public void deleteOrderItem(Long id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem not found with id " + id));
        orderItemRepository.delete(orderItem);
    }
}