package com.example.backend.service;

import com.example.backend.entity.Order;
import com.example.backend.entity.OrderItem;
import com.example.backend.entity.Product;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.OrderStatus;
import com.example.backend.repository.OrderItemRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderItemRepository orderItemRepository; // Although not directly used in denyOrder, it's a dependency of OrderService

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private Product product1;
    private Product product2;
    private OrderItem orderItem1;
    private OrderItem orderItem2;

    @BeforeEach
    void setUp() {
        // Initialize products
        product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setStock(10);

        product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setStock(5);

        // Initialize order items
        orderItem1 = new OrderItem();
        orderItem1.setProduct(product1);
        orderItem1.setQuantity(2);

        orderItem2 = new OrderItem();
        orderItem2.setProduct(product2);
        orderItem2.setQuantity(3);

        // Initialize order
        order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderItems(Arrays.asList(orderItem1, orderItem2));
    }

    @Test
    void denyOrder_shouldChangeStatusToCanceledAndReturnStock() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order deniedOrder = orderService.denyOrder(1L);

        assertNotNull(deniedOrder);
        assertEquals(OrderStatus.CANCELED, deniedOrder.getStatus());
        assertEquals(12, product1.getStock()); // Original stock 10 + 2 from orderItem1
        assertEquals(8, product2.getStock());  // Original stock 5 + 3 from orderItem2

        verify(orderRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(product1);
        verify(productRepository, times(1)).save(product2);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void denyOrder_shouldThrowResourceNotFoundException_whenOrderNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.denyOrder(1L));

        verify(orderRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
        verify(orderRepository, never()).save(any(Order.class));
    }
}
