package com.example.backend.repository;

import com.example.backend.entity.OrderItem;
import com.example.backend.entity.Product; // Import Product
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Import List

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByProduct(Product product); // New method
}
