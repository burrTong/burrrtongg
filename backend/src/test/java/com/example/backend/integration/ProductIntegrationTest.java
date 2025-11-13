package com.example.backend.integration;

import com.example.backend.config.TestConfig;
import com.example.backend.entity.Category;
import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.model.Role;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private User testSeller;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        // Clean up
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        
        // Create test seller
        testSeller = new User();
        testSeller.setUsername("testseller" + System.currentTimeMillis());
        testSeller.setPassword("password");
        testSeller.setRole(Role.SELLER);
        testSeller = userRepository.save(testSeller);

        // Create test category
        testCategory = new Category();
        testCategory.setName("Test Category");
        testCategory = categoryRepository.save(testCategory);

        // Create test products
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setDescription("Description 1");
        product1.setPrice(99.99);
        product1.setStock(10);
        product1.setCategory(testCategory);
        product1.setSeller(testSeller);
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setDescription("Description 2");
        product2.setPrice(149.99);
        product2.setStock(5);
        product2.setCategory(testCategory);
        product2.setSeller(testSeller);
        productRepository.save(product2);
    }

    @Test
    void getAllProducts_shouldReturnAllProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[1].name").value("Product 2"));
    }
}
