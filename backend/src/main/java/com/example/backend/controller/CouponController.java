
package com.example.backend.controller;

import com.example.backend.entity.Coupon;
import com.example.backend.service.CouponService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private static final Logger log = LoggerFactory.getLogger(CouponController.class);
    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping
    public Coupon createCoupon(@RequestBody Coupon coupon) {
        log.info("Creating coupon: {}", coupon.getCode());
        Coupon created = couponService.createCoupon(coupon);
        log.info("Coupon created with id: {}", created.getId());
        return created;
    }

    @GetMapping("/{code}")
    public Coupon getCouponByCode(@PathVariable String code) {
        log.info("Fetching coupon by code: {}", code);
        return couponService.getCouponByCode(code);
    }

    @GetMapping
    public List<Coupon> getAllCoupons() {
        log.info("Fetching all coupons");
        List<Coupon> coupons = couponService.getAllCoupons();
        log.info("Retrieved {} coupons", coupons.size());
        return coupons;
    }

    @GetMapping("/active")
    public List<Coupon> getActiveCoupons() {
        log.info("Fetching active coupons");
        List<Coupon> coupons = couponService.getActiveCoupons();
        log.info("Retrieved {} active coupons", coupons.size());
        return coupons;
    }

    @PutMapping("/{id}")
    public Coupon updateCoupon(@PathVariable Long id, @RequestBody Coupon coupon) {
        log.info("Updating coupon id: {}", id);
        Coupon updated = couponService.updateCoupon(id, coupon);
        log.info("Coupon id: {} updated successfully", id);
        return updated;
    }

    @DeleteMapping("/{id}")
    public String deleteCoupon(@PathVariable Long id) {
        log.info("Deleting coupon id: {}", id);
        couponService.deleteCoupon(id);
        log.info("Coupon id: {} deleted successfully", id);
        return "Coupon deleted with id " + id;
    }
}
