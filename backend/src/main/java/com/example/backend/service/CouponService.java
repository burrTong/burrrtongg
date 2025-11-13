
package com.example.backend.service;

import com.example.backend.entity.Coupon;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CouponRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponService {

    private static final Logger log = LoggerFactory.getLogger(CouponService.class);
    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public Coupon createCoupon(Coupon coupon) {
        log.info("Creating coupon: {} (type: {}, value: {})", 
                coupon.getCode(), coupon.getDiscountType(), coupon.getDiscountValue());
        Coupon saved = couponRepository.save(coupon);
        log.info("Coupon created successfully with id: {}", saved.getId());
        return saved;
    }

    public Coupon getCouponByCode(String code) {
        log.info("Fetching coupon by code: {}", code);
        return couponRepository.findByCode(code)
                .orElseThrow(() -> {
                    log.warn("Coupon not found with code: {}", code);
                    return new ResourceNotFoundException("Coupon not found with code " + code);
                });
    }

    public List<Coupon> getAllCoupons() {
        log.info("Fetching all coupons from database");
        return couponRepository.findAll();
    }

    public List<Coupon> getActiveCoupons() {
        log.info("Fetching active coupons");
        List<Coupon> activeCoupons = couponRepository.findActiveCoupons(LocalDateTime.now());
        log.info("Found {} active coupons", activeCoupons.size());
        return activeCoupons;
    }

    public Coupon updateCoupon(Long id, Coupon updatedCoupon) {
        log.info("Updating coupon id: {}", id);
        return couponRepository.findById(id)
                .map(coupon -> {
                    coupon.setCode(updatedCoupon.getCode());
                    coupon.setDiscountType(updatedCoupon.getDiscountType());
                    coupon.setDiscountValue(updatedCoupon.getDiscountValue());
                    coupon.setExpirationDate(updatedCoupon.getExpirationDate());
                    coupon.setMaxUses(updatedCoupon.getMaxUses());
                    coupon.setMinPurchaseAmount(updatedCoupon.getMinPurchaseAmount());
                    coupon.setActive(updatedCoupon.isActive());
                    Coupon saved = couponRepository.save(coupon);
                    log.info("Coupon id: {} updated successfully", id);
                    return saved;
                })
                .orElseThrow(() -> {
                    log.warn("Coupon not found for update with id: {}", id);
                    return new ResourceNotFoundException("Coupon not found with id " + id);
                });
    }

    public void deleteCoupon(Long id) {
        log.info("Deleting coupon id: {}", id);
        if (!couponRepository.existsById(id)) {
            log.warn("Coupon not found for deletion with id: {}", id);
            throw new ResourceNotFoundException("Coupon not found with id " + id);
        }
        couponRepository.deleteById(id);
        log.info("Coupon id: {} deleted successfully", id);
    }
}
