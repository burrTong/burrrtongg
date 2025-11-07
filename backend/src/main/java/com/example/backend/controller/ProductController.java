package com.example.backend.controller;

import com.example.backend.entity.Product;
import com.example.backend.service.ProductService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import com.example.backend.model.dto.ProductRequest;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String name) {
        return productService.searchProducts(name);
    }

    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategory(@PathVariable Long categoryId) {
        return productService.getProductsByCategory(categoryId);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestPart("product") ProductRequest productRequest,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        Product createdProduct = productService.createProduct(productRequest, imageFile);
        return ResponseEntity.ok(createdProduct);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") ProductRequest productRequest,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        Product updatedProduct = productService.updateProduct(id, productRequest, imageFile);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "Product deleted with id " + id;
    }

    @GetMapping("/stock-report")
    public ResponseEntity<List<com.example.backend.model.dto.WeeklyStockReportDTO>> getWeeklyStockReport() {
        return ResponseEntity.ok(productService.getWeeklyStockReport());
    }
}
