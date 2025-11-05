
package com.example.backend.controller;

import com.example.backend.entity.Coupon;
import com.example.backend.service.CouponService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping
    public Coupon createCoupon(@RequestBody Coupon coupon) {
        return couponService.createCoupon(coupon);
    }

    @GetMapping("/{code}")
    public Coupon getCouponByCode(@PathVariable String code) {
        return couponService.getCouponByCode(code);
    }

    @GetMapping
    public List<Coupon> getAllCoupons() {
        return couponService.getAllCoupons();
    }

    @GetMapping("/active")
    public List<Coupon> getActiveCoupons() {
        return couponService.getActiveCoupons();
    }

    @PutMapping("/{id}")
    public Coupon updateCoupon(@PathVariable Long id, @RequestBody Coupon coupon) {
        return couponService.updateCoupon(id, coupon);
    }

    @DeleteMapping("/{id}")
    public String deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return "Coupon deleted with id " + id;
    }
}
