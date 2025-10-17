package com.example.backend.service;

import com.example.backend.entity.Category;
import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.dto.ProductRequest;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;
    private User adminUser;
    private Category category;

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setId(1L);
        product1.setName("Test Product 1");
        product1.setDescription("Description 1");
        product1.setPrice(10.0);
        product1.setStock(100);
        product1.setImageUrl("/assets/product1.png");

        product2 = new Product();
        product2.setId(2L);
        product2.setName("Test Product 2");
        product2.setDescription("Description 2");
        product2.setPrice(20.0);
        product2.setStock(50);
        product2.setImageUrl("/assets/product2.png");

        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin@admin.com");

        category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        // Mock the UPLOAD_DIR creation in ProductService constructor
        try {
            Path uploadDirPath = Paths.get("uploads/images");
            if (!Files.exists(uploadDirPath)) {
                Files.createDirectories(uploadDirPath);
            }
        } catch (IOException e) {
            fail("Failed to create upload directory for test setup: " + e.getMessage());
        }
    }

    @Test
    void getAllProducts_shouldReturnAllProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<Product> products = productService.getAllProducts();

        assertNotNull(products);
        assertEquals(2, products.size());
        assertTrue(products.contains(product1));
        assertTrue(products.contains(product2));
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductById_shouldReturnProduct_whenFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        Product foundProduct = productService.getProductById(1L);

        assertNotNull(foundProduct);
        assertEquals(product1.getName(), foundProduct.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_shouldThrowResourceNotFoundException_whenNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void createProduct_shouldCreateProduct_withImageFile() throws IOException {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("New Product");
        productRequest.setDescription("New Description");
        productRequest.setPrice(50.0);
        productRequest.setStock(200);
        productRequest.setCategoryId(1L);

        MockMultipartFile imageFile = new MockMultipartFile("image", "test.png", "image/png", "test data".getBytes());

        when(userRepository.findByUsername("admin@admin.com")).thenReturn(Optional.of(adminUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            savedProduct.setId(3L); // Simulate ID generation
            return savedProduct;
        });

        Product createdProduct = productService.createProduct(productRequest, imageFile);

        assertNotNull(createdProduct);
        assertEquals("New Product", createdProduct.getName());
        assertTrue(createdProduct.getImageUrl().contains("uploads/images/"));
        assertEquals(adminUser, createdProduct.getSeller());
        assertEquals(category, createdProduct.getCategory());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void createProduct_shouldCreateProduct_withImageUrl() {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("New Product");
        productRequest.setDescription("New Description");
        productRequest.setPrice(50.0);
        productRequest.setStock(200);
        productRequest.setImageUrl("/custom/image.jpg");
        productRequest.setCategoryId(1L);

        when(userRepository.findByUsername("admin@admin.com")).thenReturn(Optional.of(adminUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            savedProduct.setId(3L); // Simulate ID generation
            return savedProduct;
        });

        Product createdProduct = productService.createProduct(productRequest, null);

        assertNotNull(createdProduct);
        assertEquals("New Product", createdProduct.getName());
        assertEquals("/custom/image.jpg", createdProduct.getImageUrl());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void createProduct_shouldCreateProduct_withDefaultImage() {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("New Product");
        productRequest.setDescription("New Description");
        productRequest.setPrice(50.0);
        productRequest.setStock(200);
        productRequest.setCategoryId(1L);

        when(userRepository.findByUsername("admin@admin.com")).thenReturn(Optional.of(adminUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            savedProduct.setId(3L); // Simulate ID generation
            return savedProduct;
        });

        Product createdProduct = productService.createProduct(productRequest, null);

        assertNotNull(createdProduct);
        assertEquals("New Product", createdProduct.getName());
        assertEquals("/assets/product.png", createdProduct.getImageUrl());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void createProduct_shouldThrowResourceNotFoundException_whenAdminUserNotFound() {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("New Product");
        productRequest.setCategoryId(1L);

        when(userRepository.findByUsername("admin@admin.com")).thenReturn(Optional.empty());
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(productRequest, null));
        verify(userRepository, times(1)).findByUsername("admin@admin.com");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void createProduct_shouldThrowResourceNotFoundException_whenCategoryNotFound() {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("New Product");
        productRequest.setCategoryId(99L);

        // Removed: when(userRepository.findByUsername("admin@admin.com")).thenReturn(Optional.of(adminUser));
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(productRequest, null));
        verify(categoryRepository, times(1)).findById(99L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateProduct_shouldUpdateProduct_whenFound() {
        Product productDetails = new Product();
        productDetails.setName("Updated Name");
        productDetails.setDescription("Updated Description");
        productDetails.setPrice(15.0);
        productDetails.setStock(110);
        productDetails.setImageUrl("/updated/image.png");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product updatedProduct = productService.updateProduct(1L, productDetails);

        assertNotNull(updatedProduct);
        assertEquals("Updated Name", updatedProduct.getName());
        assertEquals(110, updatedProduct.getStock());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_shouldThrowResourceNotFoundException_whenNotFound() {
        Product productDetails = new Product();
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(1L, productDetails));
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_shouldDeleteProduct_whenFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteProduct_shouldThrowResourceNotFoundException_whenNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    void isOwner_shouldReturnTrue_whenUserIsOwner() {
        product1.setSeller(adminUser);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        assertTrue(productService.isOwner(1L, "admin@admin.com"));
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void isOwner_shouldReturnFalse_whenUserIsNotOwner() {
        User otherUser = new User();
        otherUser.setUsername("other@user.com");
        product1.setSeller(otherUser);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        assertFalse(productService.isOwner(1L, "admin@admin.com"));
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void isOwner_shouldReturnFalse_whenProductNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertFalse(productService.isOwner(1L, "admin@admin.com"));
        verify(productRepository, times(1)).findById(1L);
    }
}
