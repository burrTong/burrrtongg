package com.example.backend.controller;

import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import com.example.backend.service.StockReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    private final UserService userService;
    private final StockReportService stockReportService;

    public AdminController(UserService userService, StockReportService stockReportService) {
        this.userService = userService;
        this.stockReportService = stockReportService;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Placeholder for dashboard data
    public static class Dashboard {
        public int totalSellers;
        public int totalCustomers;
        public double totalSales;
        public String bestSellingProduct;
    }

    @GetMapping("/dashboard")
    public Dashboard getDashboard() {
        // In a real application, you would calculate this data.
        Dashboard dashboard = new Dashboard();
        dashboard.totalSellers = 10;
        dashboard.totalCustomers = 100;
        dashboard.totalSales = 50000.0;
        dashboard.bestSellingProduct = "Running Shoes";
        return dashboard;
    }

    @GetMapping("/stock-report")
    public List<Map<String, Object>> getWeeklyStockReport() {
        return stockReportService.getWeeklyStockReport();
    }
}
