package com.example.backend.service;

import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;
// import org.springframework.security.access.prepost.PreAuthorize; // Commented out
// import org.springframework.security.core.userdetails.UserDetails; // Commented out
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
    }

    public Product createProduct(Product product) { // Removed UserDetails parameter
        // Temporarily find an admin user to set as seller
        User seller = userRepository.findByUsername("admin@admin.com") // Hardcoded admin user
                .orElseThrow(() -> new ResourceNotFoundException("Default admin user not found. Please create 'admin@admin.com' in database."));
        product.setSeller(seller);
        return productRepository.save(product);
    }

    // @PreAuthorize("@productService.isOwner(#id, principal.username) or hasAuthority('ADMIN')") // Commented out
    public Product updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(productDetails.getName());
                    product.setDescription(productDetails.getDescription());
                    product.setPrice(productDetails.getPrice());
                    product.setStock(productDetails.getStock());
                    product.setImageUrl(productDetails.getImageUrl());
                    product.setSize(productDetails.getSize());
                    return productRepository.save(product);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
    }

    // @PreAuthorize("@productService.isOwner(#id, principal.username) or hasAuthority('ADMIN')") // Commented out
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public boolean isOwner(Long productId, String username) {
        return productRepository.findById(productId)
                .map(product -> product.getSeller().getUsername().equals(username))
                .orElse(false);
    }
}