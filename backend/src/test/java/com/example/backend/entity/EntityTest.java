package com.example.backend.entity;

import com.example.backend.model.OrderStatus;
import com.example.backend.model.Role;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    @Test
    void user_gettersAndSetters() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        assertEquals(1L, user.getId());
        assertEquals("user@example.com", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals(Role.CUSTOMER, user.getRole());
    }

    @Test
    void category_gettersAndSetters() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        assertEquals(1L, category.getId());
        assertEquals("Electronics", category.getName());
    }

    @Test
    void product_gettersAndSetters() {
        Product product = new Product();
        Category category = new Category();
        category.setId(1L);
        User seller = new User();
        seller.setId(1L);

        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Description");
        product.setPrice(100.0);
        product.setStock(50);
        product.setSize("M");
        product.setImageUrl("/image.png");
        product.setCategory(category);
        product.setSeller(seller);

        assertEquals(1L, product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals("Description", product.getDescription());
        assertEquals(100.0, product.getPrice());
        assertEquals(50, product.getStock());
        assertEquals("M", product.getSize());
        assertEquals("/image.png", product.getImageUrl());
        assertNotNull(product.getCategory());
        assertNotNull(product.getSeller());
    }

    @Test
    void coupon_gettersAndSetters() {
        Coupon coupon = new Coupon();
        coupon.setId(1L);
        coupon.setCode("SAVE10");
        coupon.setDiscountValue(BigDecimal.valueOf(10));
        coupon.setActive(true);
        coupon.setExpirationDate(LocalDateTime.now().plusDays(30));

        assertEquals(1L, coupon.getId());
        assertEquals("SAVE10", coupon.getCode());
        assertEquals(BigDecimal.valueOf(10), coupon.getDiscountValue());
        assertTrue(coupon.isActive());
        assertNotNull(coupon.getExpirationDate());
    }

    @Test
    void order_gettersAndSetters() {
        Order order = new Order();
        User customer = new User();
        customer.setId(1L);
        Coupon coupon = new Coupon();
        coupon.setId(1L);
        List<OrderItem> items = new ArrayList<>();

        order.setId(1L);
        order.setCustomer(customer);
        order.setTotalPrice(100.0);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setCoupon(coupon);
        order.setOrderItems(items);

        assertEquals(1L, order.getId());
        assertNotNull(order.getCustomer());
        assertEquals(100.0, order.getTotalPrice());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertNotNull(order.getOrderDate());
        assertNotNull(order.getCoupon());
        assertNotNull(order.getOrderItems());
    }

    @Test
    void orderItem_gettersAndSetters() {
        OrderItem orderItem = new OrderItem();
        Product product = new Product();
        product.setId(1L);
        Order order = new Order();
        order.setId(1L);

        orderItem.setId(1L);
        orderItem.setProduct(product);
        orderItem.setOrder(order);
        orderItem.setQuantity(5);
        orderItem.setPrice(50.0);

        assertEquals(1L, orderItem.getId());
        assertNotNull(orderItem.getProduct());
        assertNotNull(orderItem.getOrder());
        assertEquals(5, orderItem.getQuantity());
        assertEquals(50.0, orderItem.getPrice());
    }

    @Test
    void user_roleEnum() {
        User adminUser = new User();
        adminUser.setRole(Role.ADMIN);
        assertEquals(Role.ADMIN, adminUser.getRole());

        User normalUser = new User();
        normalUser.setRole(Role.CUSTOMER);
        assertEquals(Role.CUSTOMER, normalUser.getRole());
    }

    @Test
    void order_statusEnum() {
        Order order = new Order();
        
        order.setStatus(OrderStatus.PENDING);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        
        order.setStatus(OrderStatus.PROCESSING);
        assertEquals(OrderStatus.PROCESSING, order.getStatus());
        
        order.setStatus(OrderStatus.SHIPPED);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        
        order.setStatus(OrderStatus.DELIVERED);
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
        
        order.setStatus(OrderStatus.CANCELED);
        assertEquals(OrderStatus.CANCELED, order.getStatus());
    }

    @Test
    void product_nullCategory() {
        Product product = new Product();
        product.setCategory(null);
        assertNull(product.getCategory());
    }

    @Test
    void order_nullCoupon() {
        Order order = new Order();
        order.setCoupon(null);
        assertNull(order.getCoupon());
    }

    @Test
    void coupon_isActiveFlag() {
        Coupon activeCoupon = new Coupon();
        activeCoupon.setActive(true);
        assertTrue(activeCoupon.isActive());

        Coupon inactiveCoupon = new Coupon();
        inactiveCoupon.setActive(false);
        assertFalse(inactiveCoupon.isActive());
    }

    @Test
    void order_emptyItemsList() {
        Order order = new Order();
        order.setOrderItems(new ArrayList<>());
        assertNotNull(order.getOrderItems());
        assertTrue(order.getOrderItems().isEmpty());
    }

    @Test
    void product_priceValidation() {
        Product product = new Product();
        product.setPrice(0.0);
        assertEquals(0.0, product.getPrice());

        product.setPrice(999.99);
        assertEquals(999.99, product.getPrice());
    }

    @Test
    void product_stockValidation() {
        Product product = new Product();
        product.setStock(0);
        assertEquals(0, product.getStock());

        product.setStock(100);
        assertEquals(100, product.getStock());
    }
}
