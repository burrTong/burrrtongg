package com.example.backend.service;

import com.example.backend.entity.Category;
import com.example.backend.entity.Order;
import com.example.backend.entity.OrderItem;
import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.OrderStatus;
import com.example.backend.model.dto.ProductRequest;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.OrderItemRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository; // New
    private final String UPLOAD_DIR = "uploads/images";

    public ProductService(ProductRepository productRepository, UserRepository userRepository, CategoryRepository categoryRepository, OrderItemRepository orderItemRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository; // Initialize
        // Ensure upload directory exists
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
    }

    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + categoryId));
        return productRepository.findByCategory(category);
    }

    public Product createProduct(ProductRequest productRequest, MultipartFile imageFile) {
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
                product.setImageUrl("/" + UPLOAD_DIR + "/" + fileName); // Store relative path
            } catch (IOException e) {
                System.err.println("Failed to store image file: " + e.getMessage());
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
        }

        // Temporarily find an admin user to set as seller
        User seller = userRepository.findByUsername("admin@admin.com") // Hardcoded admin user
                .orElseThrow(() -> new ResourceNotFoundException("Default admin user not found. Please create 'admin@admin.com' in database."));
        product.setSeller(seller);

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductRequest productRequest, MultipartFile imageFile) {
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
                product.setImageUrl("/" + UPLOAD_DIR + "/" + fileName); // Store relative path
            } catch (IOException e) {
                System.err.println("Failed to store image file: " + e.getMessage());
                throw new RuntimeException("Failed to store image file", e);
            }
        }

        // Set Category
        if (productRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + productRequest.getCategoryId()));
            product.setCategory(category);
        }

        return productRepository.save(product);
    }

    // @PreAuthorize("@productService.isOwner(#id, principal.username) or hasAuthority('ADMIN')") // Commented out
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));

        // Delete associated order items first
        List<OrderItem> orderItems = orderItemRepository.findByProduct(product);
        orderItemRepository.deleteAll(orderItems);

        productRepository.delete(product); // Delete the product
    }

    public boolean isOwner(Long productId, String username) {
        return productRepository.findById(productId)
                .map(product -> product.getSeller().getUsername().equals(username))
                .orElse(false);
    }

    public List<com.example.backend.model.dto.WeeklyStockReportDTO> getWeeklyStockReport() {
        java.time.LocalDateTime sevenDaysAgo = java.time.LocalDateTime.now().minusDays(7);

        List<Order> recentOrders = orderRepository.findByOrderDateAfter(sevenDaysAgo);

        java.util.Map<Long, Integer> totalOrdersMap = new java.util.HashMap<>();
        java.util.Map<Long, Integer> acceptedOrdersMap = new java.util.HashMap<>();
        java.util.Map<Long, Integer> deniedOrdersMap = new java.util.HashMap<>();

        for (Order order : recentOrders) {
            for (OrderItem item : order.getOrderItems()) {
                Long productId = item.getProduct().getId();
                totalOrdersMap.merge(productId, item.getQuantity(), Integer::sum);

                if (order.getStatus() == OrderStatus.DELIVERED) {
                    acceptedOrdersMap.merge(productId, item.getQuantity(), Integer::sum);
                } else if (order.getStatus() == OrderStatus.CANCELED) {
                    deniedOrdersMap.merge(productId, item.getQuantity(), Integer::sum);
                }
            }
        }

        List<Product> allProducts = productRepository.findAll();
        java.util.List<com.example.backend.model.dto.WeeklyStockReportDTO> report = new java.util.ArrayList<>();

        for (Product product : allProducts) {
            Integer totalOrders = totalOrdersMap.getOrDefault(product.getId(), 0);
            Integer acceptedOrders = acceptedOrdersMap.getOrDefault(product.getId(), 0);
            Integer deniedOrders = deniedOrdersMap.getOrDefault(product.getId(), 0);

            report.add(new com.example.backend.model.dto.WeeklyStockReportDTO(
                    product.getId(),
                    product.getName(),
                    product.getStock(), // initialStock now represents current stock
                    totalOrders,
                    acceptedOrders,
                    deniedOrders
            ));
        }

        return report;
    }
}