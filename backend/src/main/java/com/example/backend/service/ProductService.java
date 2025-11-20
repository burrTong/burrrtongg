package com.example.backend.service;

import com.example.backend.entity.Category;
import com.example.backend.entity.OrderItem;
import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.dto.ProductRequest;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.OrderItemRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;
import com.example.elasticsearch.ProductDocument;
import com.example.elasticsearch.service.ProductSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final OrderItemRepository orderItemRepository; // New
    private final ProductSearchService productSearchService;
    private final String UPLOAD_DIR = "/app/uploads/images";

    public ProductService(ProductRepository productRepository, UserRepository userRepository, CategoryRepository categoryRepository, OrderItemRepository orderItemRepository, ProductSearchService productSearchService) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.orderItemRepository = orderItemRepository; // Initialize
        this.productSearchService = productSearchService;
        // Ensure upload directory exists (skip in test environment)
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Upload directory created: {}", UPLOAD_DIR);
            } else {
                log.debug("Upload directory already exists: {}", UPLOAD_DIR);
            }
        } catch (IOException e) {
            // Don't fail in test environment where /app directory may not be writable
            log.warn("Could not create upload directory: {} - {}", UPLOAD_DIR, e.getMessage());
        }
    }

    @PostConstruct
    public void indexProducts() {
        log.info("Indexing all products to Elasticsearch...");
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            ProductDocument productDocument = new ProductDocument();
            productDocument.setId(product.getId().toString());
            productDocument.setName(product.getName());
            productDocument.setDescription(product.getDescription());
            productSearchService.saveProduct(productDocument);
        }
        log.info("Indexed {} products to Elasticsearch", products.size());
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
    }

    public List<Product> searchProducts(String name) {
        Iterable<ProductDocument> documents = productSearchService.searchProducts(name);
        List<Long> ids = StreamSupport.stream(documents.spliterator(), false)
                .map(doc -> Long.parseLong(doc.getId()))
                .collect(Collectors.toList());
        return productRepository.findAllById(ids);
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + categoryId));
        return productRepository.findByCategory(category);
    }

    public Product createProduct(ProductRequest productRequest, MultipartFile imageFile) {
        log.info("Creating product: {}", productRequest.getName());
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStock(productRequest.getStock());
        product.setSize(productRequest.getSize());

        // Handle image upload
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR, fileName);
                Files.copy(imageFile.getInputStream(), filePath);
                product.setImageUrl("/uploads/images/" + fileName); // Store relative path for URL
                log.info("Image uploaded successfully: {}", fileName);
            } catch (IOException e) {
                log.error("Failed to store image file: {}", imageFile.getOriginalFilename(), e);
                throw new RuntimeException("Failed to store image file", e);
            }
        } else if (productRequest.getImageUrl() != null && !productRequest.getImageUrl().isEmpty()) {
            product.setImageUrl(productRequest.getImageUrl()); // Use imageUrl from request if no file uploaded
        } else {
            product.setImageUrl("/assets/product.png"); // Default image if no file and no imageUrl in request
        }

        // Set Category
        if (productRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + productRequest.getCategoryId()));
            product.setCategory(category);
            log.info("Product assigned to category: {}", category.getName());
        }

        // Temporarily find an admin user to set as seller
        User seller = userRepository.findByUsername("admin@admin.com") // Hardcoded admin user
                .orElseThrow(() -> new ResourceNotFoundException("Default admin user not found. Please create 'admin@admin.com' in database."));
        product.setSeller(seller);

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with id: {}", savedProduct.getId());

        ProductDocument productDocument = new ProductDocument();
        productDocument.setId(savedProduct.getId().toString());
        productDocument.setName(savedProduct.getName());
        productDocument.setDescription(savedProduct.getDescription());
        productSearchService.saveProduct(productDocument);
        log.info("Product indexed to Elasticsearch: {}", savedProduct.getId());

        return savedProduct;
    }

    public Product updateProduct(Long id, ProductRequest productRequest, MultipartFile imageFile) {
        log.info("Updating product id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStock(productRequest.getStock());
        product.setSize(productRequest.getSize());

        // Handle image upload
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR, fileName);
                Files.copy(imageFile.getInputStream(), filePath);
                product.setImageUrl("/uploads/images/" + fileName); // Store relative path for URL
                log.info("Product image updated: {}", fileName);
            } catch (IOException e) {
                log.error("Failed to update product image", e);
                throw new RuntimeException("Failed to store image file", e);
            }
        }

        // Set Category
        if (productRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + productRequest.getCategoryId()));
            product.setCategory(category);
        }

        Product savedProduct = productRepository.save(product);
        log.info("Product id: {} updated successfully", id);

        ProductDocument productDocument = new ProductDocument();
        productDocument.setId(savedProduct.getId().toString());
        productDocument.setName(savedProduct.getName());
        productDocument.setDescription(savedProduct.getDescription());
        productSearchService.saveProduct(productDocument);

        return savedProduct;
    }

    // @PreAuthorize("@productService.isOwner(#id, principal.username) or hasAuthority('ADMIN')") // Commented out
    public void deleteProduct(Long id) {
        log.info("Deleting product id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));

        // Delete associated order items first
        List<OrderItem> orderItems = orderItemRepository.findByProduct(product);
        if (!orderItems.isEmpty()) {
            log.info("Deleting {} order items associated with product id: {}", orderItems.size(), id);
            orderItemRepository.deleteAll(orderItems);
        }

        productRepository.delete(product); // Delete the product
        productSearchService.deleteProduct(id.toString());
        log.info("Product id: {} deleted successfully", id);
    }

    public boolean isOwner(Long productId, String username) {
        return productRepository.findById(productId)
                .map(product -> product.getSeller().getUsername().equals(username))
                .orElse(false);
    }
}