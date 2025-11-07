package com.example.backend.repository;

import com.example.backend.entity.Order;
import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomer(User customer);
    
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi " +
           "JOIN oi.order o " +
           "WHERE oi.product.id = :productId " +
           "AND o.orderDate >= :startDate " +
           "AND o.orderDate <= :endDate " +
           "AND o.status != 'CANCELED'")
    Integer getTotalSoldQuantityByProductIdAndDateRange(@Param("productId") Long productId,
                                                       @Param("startDate") LocalDateTime startDate,
                                                       @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi " +
           "JOIN oi.order o " +
           "WHERE oi.product.id = :productId")
    Integer getTotalOrdersByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi " +
           "JOIN oi.order o " +
           "WHERE oi.product.id = :productId " +
           "AND o.status IN ('PROCESSING', 'SHIPPED', 'DELIVERED')")
    Integer getAcceptedOrdersByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi " +
           "JOIN oi.order o " +
           "WHERE oi.product.id = :productId " +
           "AND o.status = 'PENDING'")
    Integer getPendingOrdersByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi " +
           "JOIN oi.order o " +
           "WHERE oi.product.id = :productId " +
           "AND o.status = 'CANCELED'")
    Integer getDeniedOrdersByProductId(@Param("productId") Long productId);

    // Weekly date range queries
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi " +
           "JOIN oi.order o " +
           "WHERE oi.product.id = :productId " +
           "AND o.orderDate >= :startDate " +
           "AND o.orderDate <= :endDate")
    Integer getTotalOrdersByProductIdAndDateRange(@Param("productId") Long productId,
                                                 @Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi " +
           "JOIN oi.order o " +
           "WHERE oi.product.id = :productId " +
           "AND o.orderDate >= :startDate " +
           "AND o.orderDate <= :endDate " +
           "AND o.status IN ('PROCESSING', 'SHIPPED', 'DELIVERED')")
    Integer getAcceptedOrdersByProductIdAndDateRange(@Param("productId") Long productId,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi " +
           "JOIN oi.order o " +
           "WHERE oi.product.id = :productId " +
           "AND o.orderDate >= :startDate " +
           "AND o.orderDate <= :endDate " +
           "AND o.status = 'PENDING'")
    Integer getPendingOrdersByProductIdAndDateRange(@Param("productId") Long productId,
                                                   @Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi " +
           "JOIN oi.order o " +
           "WHERE oi.product.id = :productId " +
           "AND o.orderDate >= :startDate " +
           "AND o.orderDate <= :endDate " +
           "AND o.status = 'CANCELED'")
    Integer getDeniedOrdersByProductIdAndDateRange(@Param("productId") Long productId,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);
}
