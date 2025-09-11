package com.example.backend.service;

import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
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

    public Product createProduct(Product product, UserDetails userDetails) {
        User seller = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with username: " + userDetails.getUsername()));
        product.setSeller(seller);
        return productRepository.save(product);
    }

    @PreAuthorize("@productService.isOwner(#id, principal.username) or hasAuthority('ADMIN')")
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

    @PreAuthorize("@productService.isOwner(#id, principal.username) or hasAuthority('ADMIN')")
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public boolean isOwner(Long productId, String username) {
        return productRepository.findById(productId)
                .map(product -> product.getSeller().getUsername().equals(username))
                .orElse(false);
    }
}
