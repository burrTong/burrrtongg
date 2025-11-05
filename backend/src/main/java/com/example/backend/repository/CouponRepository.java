
package com.example.backend.repository;

import com.example.backend.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCode(String code);
    
    // Find active coupons that are not expired and still have usage left
    @Query("SELECT c FROM Coupon c WHERE c.isActive = true AND " +
           "(c.expirationDate IS NULL OR c.expirationDate > :currentTime) AND " +
           "(c.maxUses IS NULL OR c.timesUsed < c.maxUses)")
    List<Coupon> findActiveCoupons(LocalDateTime currentTime);
}
