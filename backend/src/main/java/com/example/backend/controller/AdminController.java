package com.example.backend.controller;

import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
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
}
