package com.example.backend.model.dto;

public class WeeklyStockReportDTO {
    private Long id;
    private String productName;
    private Integer initialStock;
    private Integer totalOrders;
    private Integer acceptedOrders;
    private Integer deniedOrders;

    public WeeklyStockReportDTO(Long id, String productName, Integer initialStock, Integer totalOrders, Integer acceptedOrders, Integer deniedOrders) {
        this.id = id;
        this.productName = productName;
        this.initialStock = initialStock;
        this.totalOrders = totalOrders;
        this.acceptedOrders = acceptedOrders;
        this.deniedOrders = deniedOrders;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getInitialStock() {
        return initialStock;
    }

    public void setInitialStock(Integer initialStock) {
        this.initialStock = initialStock;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Integer getAcceptedOrders() {
        return acceptedOrders;
    }

    public void setAcceptedOrders(Integer acceptedOrders) {
        this.acceptedOrders = acceptedOrders;
    }

    public Integer getDeniedOrders() {
        return deniedOrders;
    }

    public void setDeniedOrders(Integer deniedOrders) {
        this.deniedOrders = deniedOrders;
    }
}
