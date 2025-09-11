package com.example.backend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @GetMapping
    public String getAllCustomers() {
        return "List of all customers";
    }

    @GetMapping("/{id}")
    public String getCustomerById(@PathVariable Long id) {
        return "Customer with ID: " + id;
    }

    @PostMapping
    public String createCustomer() {
        return "New customer created";
    }

    @PutMapping("/{id}")
    public String updateCustomer(@PathVariable Long id) {
        return "Customer updated with ID: " + id;
    }

    @DeleteMapping("/{id}")
    public String deleteCustomer(@PathVariable Long id) {
        return "Customer deleted with ID: " + id;
    }
}
