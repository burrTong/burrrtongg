package com.example.backend.integration;

import com.example.backend.config.TestConfig;
import com.example.backend.entity.*;
import com.example.backend.model.OrderStatus;
import com.example.backend.model.Role;
import com.example.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User testCustomer;

    @BeforeEach
    void setUp() {
        // Clean up
        orderRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        
        // Create test customer
        testCustomer = new User();
        testCustomer.setUsername("testcustomer" + System.currentTimeMillis());
        testCustomer.setPassword("password");
        testCustomer.setRole(Role.CUSTOMER);
        testCustomer = userRepository.save(testCustomer);

        // Create test seller
        User testSeller = new User();
        testSeller.setUsername("testseller" + System.currentTimeMillis());
        testSeller.setPassword("password");
        testSeller.setRole(Role.SELLER);
        testSeller = userRepository.save(testSeller);

        // Create test category
        Category testCategory = new Category();
        testCategory.setName("Test Category");
        testCategory = categoryRepository.save(testCategory);

        // Create test product
        Product testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setPrice(99.99);
        testProduct.setStock(10);
        testProduct.setCategory(testCategory);
        testProduct.setSeller(testSeller);
        testProduct = productRepository.save(testProduct);

        // Create test orders
        Order order1 = new Order();
        order1.setCustomer(testCustomer);
        order1.setOrderDate(LocalDateTime.now());
        order1.setStatus(OrderStatus.PENDING);
        order1.setTotalPrice(99.99);
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setCustomer(testCustomer);
        order2.setOrderDate(LocalDateTime.now());
        order2.setStatus(OrderStatus.PROCESSING);
        order2.setTotalPrice(149.99);
        orderRepository.save(order2);
    }

    @Test
    void getAllOrders_shouldReturnAllOrders() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[1].status").value("PROCESSING"));
    }
}
