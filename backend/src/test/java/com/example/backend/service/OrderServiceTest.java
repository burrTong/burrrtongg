package com.example.backend.service;

import com.example.backend.entity.Order;
import com.example.backend.entity.OrderItem;
import com.example.backend.entity.Product;
import com.example.backend.entity.Coupon;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.OrderStatus;
import com.example.backend.repository.OrderItemRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.CouponRepository;
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
    @Mock
    private CouponRepository couponRepository;

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

    @Test
    void denyOrder_shouldDecreaseCouponUsage_whenOrderHasCoupon() {
        // สร้าง coupon สำหรับทดสอบ
        Coupon coupon = new Coupon();
        coupon.setId(1L);
        coupon.setCode("TEST10");
        coupon.setTimesUsed(5); // เริ่มต้น 5 ครั้ง

        // ตั้งค่า order ให้มี coupon
        order.setCoupon(coupon);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order deniedOrder = orderService.denyOrder(1L);

        assertNotNull(deniedOrder);
        assertEquals(OrderStatus.CANCELED, deniedOrder.getStatus());
        assertEquals(4, coupon.getTimesUsed()); // ลดลงจาก 5 เป็น 4

        verify(couponRepository, times(1)).save(coupon);
    }

    @Test
    void denyOrder_shouldNotDecreaseCouponUsage_whenTimesUsedIsZero() {
        // สร้าง coupon ที่ timesUsed เป็น 0
        Coupon coupon = new Coupon();
        coupon.setId(1L);
        coupon.setCode("TEST10");
        coupon.setTimesUsed(0); // เริ่มต้น 0 ครั้ง

        // ตั้งค่า order ให้มี coupon
        order.setCoupon(coupon);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order deniedOrder = orderService.denyOrder(1L);

        assertNotNull(deniedOrder);
        assertEquals(OrderStatus.CANCELED, deniedOrder.getStatus());
        assertEquals(0, coupon.getTimesUsed()); // ยังคงเป็น 0

        // ไม่ควรเรียก couponRepository.save() เพราะไม่ได้อัปเดต
        verify(couponRepository, never()).save(coupon);
    }
}
