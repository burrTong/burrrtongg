package com.example.backend.controller;

import com.example.backend.entity.Product;
import com.example.backend.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import com.example.backend.model.dto.ProductRequest;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        log.info("Fetching all products");
        List<Product> products = productService.getAllProducts();
        log.info("Retrieved {} products", products.size());
        return products;
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        log.info("Fetching product with id: {}", id);
        return productService.getProductById(id);
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String name) {
        log.info("Searching products with name: {}", name);
        List<Product> results = productService.searchProducts(name);
        log.info("Found {} products matching '{}'", results.size(), name);
        return results;
    }

    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategory(@PathVariable Long categoryId) {
        log.info("Fetching products for category id: {}", categoryId);
        return productService.getProductsByCategory(categoryId);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestPart("product") ProductRequest productRequest,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        log.info("Creating new product: {}", productRequest.getName());
        Product createdProduct = productService.createProduct(productRequest, imageFile);
        log.info("Product created successfully with id: {}", createdProduct.getId());
        return ResponseEntity.ok(createdProduct);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") ProductRequest productRequest,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        log.info("Updating product id: {} with name: {}", id, productRequest.getName());
        Product updatedProduct = productService.updateProduct(id, productRequest, imageFile);
        log.info("Product id: {} updated successfully", id);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id) {
        log.info("Deleting product with id: {}", id);
        productService.deleteProduct(id);
        log.info("Product id: {} deleted successfully", id);
        return "Product deleted with id " + id;
    }
}
