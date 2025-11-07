
package com.example.backend.service;

import com.example.backend.entity.Coupon;
import com.example.backend.entity.Order;
import com.example.backend.entity.OrderItem;
import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.OrderStatus;
import com.example.backend.model.dto.OrderRequest;
import com.example.backend.repository.CouponRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository, CouponRepository couponRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.couponRepository = couponRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));
    }

    public List<Order> getOrdersByCustomer(Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + customerId));
        return orderRepository.findByCustomer(customer);
    }

    @Transactional
    public Order createOrder(OrderRequest orderRequest) {
        User customer = userRepository.findById(orderRequest.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + orderRequest.getCustomerId()));

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (var itemRequest : orderRequest.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + itemRequest.getProductId()));

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItems.add(orderItem);

            totalPrice = totalPrice.add(BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(itemRequest.getQuantity())));

            // Decrease stock
            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);
        }

        order.setOrderItems(orderItems);

        // Coupon logic
        if (orderRequest.getCouponCode() != null && !orderRequest.getCouponCode().isEmpty()) {
            Coupon coupon = couponRepository.findByCode(orderRequest.getCouponCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Invalid coupon code"));

            // ตรวจสอบสถานะและวันหมดอายุของ coupon แยกกัน
            if (!coupon.isActive()) {
                throw new RuntimeException("Coupon '" + coupon.getCode() + "' is currently inactive");
            }
            
            if (coupon.getExpirationDate() != null && coupon.getExpirationDate().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Coupon '" + coupon.getCode() + "' has expired on " + 
                    coupon.getExpirationDate().toLocalDate().toString());
            }
            if (coupon.getMaxUses() != null && coupon.getTimesUsed() >= coupon.getMaxUses()) {
                throw new RuntimeException("Coupon '" + coupon.getCode() + "' has reached its maximum usage limit (" + 
                    coupon.getMaxUses() + " times). Used: " + coupon.getTimesUsed() + " times");
            }
            
            if (coupon.getMinPurchaseAmount() != null && totalPrice.compareTo(coupon.getMinPurchaseAmount()) < 0) {
                throw new RuntimeException("Order amount ฿" + totalPrice + " does not meet the minimum purchase amount of ฿" + 
                    coupon.getMinPurchaseAmount() + " required for coupon '" + coupon.getCode() + "'");
            }

            if ("FIXED".equals(coupon.getDiscountType())) {
                totalPrice = totalPrice.subtract(coupon.getDiscountValue());
            } else if ("PERCENTAGE".equals(coupon.getDiscountType())) {
                BigDecimal discount = totalPrice.multiply(coupon.getDiscountValue().divide(BigDecimal.valueOf(100)));
                totalPrice = totalPrice.subtract(discount);
            }

            coupon.setTimesUsed(coupon.getTimesUsed() + 1);
            couponRepository.save(coupon);
            order.setCoupon(coupon);
        }

        // Ensure total price does not go below zero
        totalPrice = totalPrice.max(BigDecimal.ZERO);

        order.setTotalPrice(totalPrice.doubleValue());

        return orderRepository.save(order);
    }

    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Transactional
    public Order denyOrder(Long orderId) {
        logger.info("Attempting to deny order with ID: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.error("Order not found with id {}", orderId);
                    return new ResourceNotFoundException("Order not found with id " + orderId);
                });

        // Return product quantities to stock
        for (OrderItem item : order.getOrderItems()) {
            Long productId = item.getProduct().getId();
            Integer quantityToReturn = item.getQuantity();

            logger.info("Processing OrderItem - Product ID: {}, Quantity to return: {}", productId, quantityToReturn);

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> {
                        logger.error("Product not found with id {} for order item", productId);
                        return new ResourceNotFoundException("Product not found with id " + productId);
                    });

            logger.info("Product {} - Stock before update: {}", productId, product.getStock());
            product.setStock(product.getStock() + quantityToReturn);
            productRepository.save(product);
            logger.info("Product {} - Stock after update: {}", productId, product.getStock());
        }

        // Decrease coupon usage count if coupon was used
        if (order.getCoupon() != null) {
            Coupon coupon = order.getCoupon();
            logger.info("Order used coupon '{}' - Current times used: {}", coupon.getCode(), coupon.getTimesUsed());
            
            if (coupon.getTimesUsed() > 0) {
                coupon.setTimesUsed(coupon.getTimesUsed() - 1);
                couponRepository.save(coupon);
                logger.info("Coupon '{}' usage decreased. New times used: {}", coupon.getCode(), coupon.getTimesUsed());
            } else {
                logger.warn("Coupon '{}' times used is already 0, cannot decrease further", coupon.getCode());
            }
        }

        order.setStatus(OrderStatus.CANCELED);
        logger.info("Order {} status set to CANCELED. Saving order.", orderId);
        return orderRepository.save(order);
    }

    public boolean isOwner(Long orderId, String username) {
        return orderRepository.findById(orderId)
                .map(order -> order.getCustomer().getUsername().equals(username))
                .orElse(false);
    }
}
