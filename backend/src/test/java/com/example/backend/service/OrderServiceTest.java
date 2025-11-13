package com.example.backend.service;

import com.example.backend.entity.*;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.OrderStatus;
import com.example.backend.model.Role;
import com.example.backend.model.dto.OrderItemRequest;
import com.example.backend.model.dto.OrderRequest;
import com.example.backend.repository.CouponRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
    private CouponRepository couponRepository;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;
    private Product testProduct;
    private User testCustomer;
    private Coupon testCoupon;

    @BeforeEach
    void setUp() {
        testCustomer = new User();
        testCustomer.setId(1L);
        testCustomer.setUsername("customer@example.com");
        testCustomer.setRole(Role.CUSTOMER);

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(100.0);
        testProduct.setStock(50);

        testCoupon = new Coupon();
        testCoupon.setId(1L);
        testCoupon.setCode("TEST10");
        testCoupon.setDiscountType("FIXED");
        testCoupon.setDiscountValue(new BigDecimal("10.00"));
        testCoupon.setActive(true);
        testCoupon.setExpirationDate(LocalDateTime.now().plusDays(30));
        testCoupon.setMaxUses(100);
        testCoupon.setTimesUsed(0);
        testCoupon.setMinPurchaseAmount(new BigDecimal("50.00"));

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomer(testCustomer);
        testOrder.setOrderDate(LocalDateTime.now());
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setTotalPrice(100.0);
    }

    @Test
    void getAllOrders_shouldReturnListOfOrders() {
        when(orderRepository.findAll()).thenReturn(Arrays.asList(testOrder));

        List<Order> result = orderService.getAllOrders();

        assertThat(result).hasSize(1);
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getOrderById_shouldReturnOrder_whenOrderExists() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        Order result = orderService.getOrderById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderById_shouldThrowException_whenOrderNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found with id 999");
    }

    @Test
    void getOrdersByCustomer_shouldReturnCustomerOrders() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(orderRepository.findByCustomer(testCustomer)).thenReturn(Arrays.asList(testOrder));

        List<Order> result = orderService.getOrdersByCustomer(1L);

        assertThat(result).hasSize(1);
        verify(userRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).findByCustomer(testCustomer);
    }

    @Test
    void getOrdersByCustomer_shouldThrowException_whenCustomerNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrdersByCustomer(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found with id 999");
    }

    @Test
    void createOrder_shouldCreateOrderWithoutCoupon() {
        OrderRequest request = new OrderRequest();
        request.setCustomerId(1L);

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);
        request.setItems(Arrays.asList(itemRequest));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        Order result = orderService.createOrder(request);

        assertThat(result).isNotNull();
        verify(userRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(testProduct);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_shouldThrowException_whenCustomerNotFound() {
        OrderRequest request = new OrderRequest();
        request.setCustomerId(999L);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found with id 999");
    }

    @Test
    void createOrder_shouldThrowException_whenProductNotFound() {
        OrderRequest request = new OrderRequest();
        request.setCustomerId(1L);

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(999L);
        itemRequest.setQuantity(1);
        request.setItems(Arrays.asList(itemRequest));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id 999");
    }

    @Test
    void createOrder_shouldThrowException_whenNotEnoughStock() {
        OrderRequest request = new OrderRequest();
        request.setCustomerId(1L);

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(100); // More than stock
        request.setItems(Arrays.asList(itemRequest));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Not enough stock");
    }

    @Test
    void createOrder_shouldApplyCoupon_whenValidCouponProvided() {
        OrderRequest request = new OrderRequest();
        request.setCustomerId(1L);
        request.setCouponCode("TEST10");

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);
        request.setItems(Arrays.asList(itemRequest));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(couponRepository.findByCode("TEST10")).thenReturn(Optional.of(testCoupon));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(couponRepository.save(any(Coupon.class))).thenReturn(testCoupon);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        Order result = orderService.createOrder(request);

        assertThat(result).isNotNull();
        verify(couponRepository, times(1)).findByCode("TEST10");
        verify(couponRepository, times(1)).save(testCoupon);
    }

    @Test
    void createOrder_shouldThrowException_whenCouponNotFound() {
        OrderRequest request = new OrderRequest();
        request.setCustomerId(1L);
        request.setCouponCode("INVALID");

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);
        request.setItems(Arrays.asList(itemRequest));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(couponRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Invalid coupon code");
    }

    @Test
    void createOrder_shouldThrowException_whenCouponIsInactive() {
        testCoupon.setActive(false);

        OrderRequest request = new OrderRequest();
        request.setCustomerId(1L);
        request.setCouponCode("TEST10");

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);
        request.setItems(Arrays.asList(itemRequest));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(couponRepository.findByCode("TEST10")).thenReturn(Optional.of(testCoupon));

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("is currently inactive");
    }

    @Test
    void updateOrderStatus_shouldUpdateStatus() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        Order result = orderService.updateOrderStatus(1L, OrderStatus.DELIVERED);

        assertThat(result).isNotNull();
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(testOrder);
    }

    @Test
    void denyOrder_shouldCancelOrderAndRestoreStock() {
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(testProduct);
        orderItem.setQuantity(2);
        testOrder.setOrderItems(Arrays.asList(orderItem));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        Order result = orderService.denyOrder(1L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELED);
        verify(productRepository, times(1)).save(testProduct);
        verify(orderRepository, times(1)).save(testOrder);
    }

    @Test
    void denyOrder_shouldDecreaseCouponUsage_whenCouponWasUsed() {
        testCoupon.setTimesUsed(1);
        testOrder.setCoupon(testCoupon);

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(testProduct);
        orderItem.setQuantity(2);
        testOrder.setOrderItems(Arrays.asList(orderItem));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(couponRepository.save(any(Coupon.class))).thenReturn(testCoupon);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        Order result = orderService.denyOrder(1L);

        assertThat(result).isNotNull();
        verify(couponRepository, times(1)).save(testCoupon);
    }
}
