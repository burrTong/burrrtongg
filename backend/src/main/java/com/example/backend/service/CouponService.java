
package com.example.backend.service;

import com.example.backend.entity.Coupon;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CouponRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    public Coupon getCouponByCode(String code) {
        return couponRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with code " + code));
    }

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public List<Coupon> getActiveCoupons() {
        return couponRepository.findActiveCoupons(LocalDateTime.now());
    }

    public Coupon updateCoupon(Long id, Coupon updatedCoupon) {
        return couponRepository.findById(id)
                .map(coupon -> {
                    coupon.setCode(updatedCoupon.getCode());
                    coupon.setDiscountType(updatedCoupon.getDiscountType());
                    coupon.setDiscountValue(updatedCoupon.getDiscountValue());
                    coupon.setExpirationDate(updatedCoupon.getExpirationDate());
                    coupon.setMaxUses(updatedCoupon.getMaxUses());
                    coupon.setMinPurchaseAmount(updatedCoupon.getMinPurchaseAmount());
                    coupon.setActive(updatedCoupon.isActive());
                    return couponRepository.save(coupon);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id " + id));
    }

    public void deleteCoupon(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new ResourceNotFoundException("Coupon not found with id " + id);
        }
        couponRepository.deleteById(id);
    }
}
