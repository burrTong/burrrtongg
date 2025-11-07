package com.example.backend.service;

import com.example.backend.entity.Product;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class StockReportService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    public List<Map<String, Object>> getWeeklyStockReport() {
        List<Product> allProducts = productRepository.findAll();
        List<Map<String, Object>> reportData = new ArrayList<>();

        LocalDateTime weekAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);

        for (Product product : allProducts) {
            Map<String, Object> stockData = new HashMap<>();
            stockData.put("id", product.getId());
            stockData.put("productName", product.getName());
            stockData.put("currentStock", product.getStock());
            
            // Calculate orders for the last 7 days
            Integer totalOrders = orderRepository.getTotalOrdersByProductIdAndDateRange(
                product.getId(), weekAgo, LocalDateTime.now());
            Integer acceptedOrders = orderRepository.getAcceptedOrdersByProductIdAndDateRange(
                product.getId(), weekAgo, LocalDateTime.now());
            Integer pendingOrders = orderRepository.getPendingOrdersByProductIdAndDateRange(
                product.getId(), weekAgo, LocalDateTime.now());
            Integer deniedOrders = orderRepository.getDeniedOrdersByProductIdAndDateRange(
                product.getId(), weekAgo, LocalDateTime.now());
            
            stockData.put("totalOrders", totalOrders != null ? totalOrders : 0);
            stockData.put("acceptedOrders", acceptedOrders != null ? acceptedOrders : 0);
            stockData.put("pendingOrders", pendingOrders != null ? pendingOrders : 0);
            stockData.put("deniedOrders", deniedOrders != null ? deniedOrders : 0);
            
            reportData.add(stockData);
        }

        return reportData;
    }
}