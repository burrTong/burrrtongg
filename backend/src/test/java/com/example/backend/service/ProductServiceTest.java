package com.example.backend.service;

import com.example.backend.entity.Category;
import com.example.backend.entity.OrderItem;
import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.Role;
import com.example.backend.model.dto.ProductRequest;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.OrderItemRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;
import com.example.elasticsearch.service.ProductSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductSearchService productSearchService;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private Category testCategory;
    private User testSeller;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Electronics");

        testSeller = new User();
        testSeller.setId(1L);
        testSeller.setUsername("admin@admin.com");
        testSeller.setRole(Role.ADMIN);

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(100.0);
        testProduct.setStock(50);
        testProduct.setSize("M");
        testProduct.setImageUrl("/assets/product.png");
        testProduct.setCategory(testCategory);
        testProduct.setSeller(testSeller);
    }

    @Test
    void getAllProducts_shouldReturnListOfProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(testProduct));

        List<Product> result = productService.getAllProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Product");
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductById_shouldReturnProduct_whenProductExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Product result = productService.getProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Product");
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_shouldThrowException_whenProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id 999");
    }

    @Test
    void getProductsByCategory_shouldReturnProductsInCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productRepository.findByCategory(testCategory)).thenReturn(Arrays.asList(testProduct));

        List<Product> result = productService.getProductsByCategory(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory().getName()).isEqualTo("Electronics");
        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findByCategory(testCategory);
    }

    @Test
    void getProductsByCategory_shouldThrowException_whenCategoryNotFound() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductsByCategory(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found with id 999");
    }

    @Test
    @Disabled("Elasticsearch dependency issue in test environment")
    void createProduct_shouldCreateProductWithoutImageFile() {
        ProductRequest request = new ProductRequest();
        request.setName("New Product");
        request.setDescription("New Description");
        request.setPrice(150.0);
        request.setStock(30);
        request.setSize("L");
        request.setCategoryId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(userRepository.findByUsername("admin@admin.com")).thenReturn(Optional.of(testSeller));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        doNothing().when(productSearchService).saveProduct(any());

        Product result = productService.createProduct(request, null);

        assertThat(result).isNotNull();
        verify(categoryRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByUsername("admin@admin.com");
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productSearchService, times(1)).saveProduct(any());
    }

    @Test
    void createProduct_shouldThrowException_whenCategoryNotFound() {
        ProductRequest request = new ProductRequest();
        request.setCategoryId(999L);

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.createProduct(request, null))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found with id 999");
    }

    @Test
    void createProduct_shouldThrowException_whenSellerNotFound() {
        ProductRequest request = new ProductRequest();
        request.setCategoryId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(userRepository.findByUsername("admin@admin.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.createProduct(request, null))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Default admin user not found");
    }

    @Test
    @Disabled("Elasticsearch dependency issue in test environment")
    void updateProduct_shouldUpdateProduct_whenProductExists() {
        ProductRequest request = new ProductRequest();
        request.setName("Updated Product");
        request.setDescription("Updated Description");
        request.setPrice(200.0);
        request.setStock(40);
        request.setSize("XL");
        request.setCategoryId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        doNothing().when(productSearchService).saveProduct(any());

        Product result = productService.updateProduct(1L, request, null);

        assertThat(result).isNotNull();
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productSearchService, times(1)).saveProduct(any());
    }

    @Test
    void updateProduct_shouldThrowException_whenProductNotFound() {
        ProductRequest request = new ProductRequest();

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct(999L, request, null))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id 999");
    }

    @Test
    void deleteProduct_shouldDeleteProductAndOrderItems() {
        OrderItem orderItem = new OrderItem();
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderItemRepository.findByProduct(testProduct)).thenReturn(Arrays.asList(orderItem));
        doNothing().when(orderItemRepository).deleteAll(any());
        doNothing().when(productRepository).delete(testProduct);
        doNothing().when(productSearchService).deleteProduct(anyString());

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).findById(1L);
        verify(orderItemRepository, times(1)).findByProduct(testProduct);
        verify(orderItemRepository, times(1)).deleteAll(any());
        verify(productRepository, times(1)).delete(testProduct);
        verify(productSearchService, times(1)).deleteProduct("1");
    }

    @Test
    void deleteProduct_shouldThrowException_whenProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProduct(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id 999");

        verify(productRepository, never()).delete(any());
    }

    @Test
    void isOwner_shouldReturnTrue_whenUserIsOwner() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        boolean result = productService.isOwner(1L, "admin@admin.com");

        assertThat(result).isTrue();
    }

    @Test
    void isOwner_shouldReturnFalse_whenUserIsNotOwner() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        boolean result = productService.isOwner(1L, "other@example.com");

        assertThat(result).isFalse();
    }

    @Test
    void isOwner_shouldReturnFalse_whenProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        boolean result = productService.isOwner(999L, "admin@admin.com");

        assertThat(result).isFalse();
    }
}
