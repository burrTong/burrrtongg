package com.example.backend.service;

import com.example.backend.entity.Coupon;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CouponRepository;
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
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    private Coupon testCoupon;

    @BeforeEach
    void setUp() {
        testCoupon = new Coupon();
        testCoupon.setId(1L);
        testCoupon.setCode("TEST10");
        testCoupon.setDiscountType("FIXED");
        testCoupon.setDiscountValue(new BigDecimal("10.00"));
        testCoupon.setExpirationDate(LocalDateTime.now().plusDays(30));
        testCoupon.setMaxUses(100);
        testCoupon.setTimesUsed(0);
        testCoupon.setMinPurchaseAmount(new BigDecimal("50.00"));
        testCoupon.setActive(true);
    }

    @Test
    void createCoupon_shouldSaveAndReturnCoupon() {
        when(couponRepository.save(any(Coupon.class))).thenReturn(testCoupon);

        Coupon result = couponService.createCoupon(testCoupon);

        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("TEST10");
        verify(couponRepository, times(1)).save(testCoupon);
    }

    @Test
    void getCouponByCode_shouldReturnCoupon_whenCouponExists() {
        when(couponRepository.findByCode("TEST10")).thenReturn(Optional.of(testCoupon));

        Coupon result = couponService.getCouponByCode("TEST10");

        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("TEST10");
        verify(couponRepository, times(1)).findByCode("TEST10");
    }

    @Test
    void getCouponByCode_shouldThrowException_whenCouponNotFound() {
        when(couponRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.getCouponByCode("INVALID"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Coupon not found with code INVALID");
    }

    @Test
    void getAllCoupons_shouldReturnListOfCoupons() {
        Coupon coupon2 = new Coupon();
        coupon2.setCode("SUMMER20");
        when(couponRepository.findAll()).thenReturn(Arrays.asList(testCoupon, coupon2));

        List<Coupon> result = couponService.getAllCoupons();

        assertThat(result).hasSize(2);
        assertThat(result).extracting("code").contains("TEST10", "SUMMER20");
        verify(couponRepository, times(1)).findAll();
    }

    @Test
    void getActiveCoupons_shouldReturnActiveCoupons() {
        when(couponRepository.findActiveCoupons(any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(testCoupon));

        List<Coupon> result = couponService.getActiveCoupons();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("TEST10");
        verify(couponRepository, times(1)).findActiveCoupons(any(LocalDateTime.class));
    }

    @Test
    void updateCoupon_shouldUpdateAndReturnCoupon_whenCouponExists() {
        Coupon updatedCoupon = new Coupon();
        updatedCoupon.setCode("UPDATED20");
        updatedCoupon.setDiscountType("PERCENTAGE");
        updatedCoupon.setDiscountValue(new BigDecimal("20.00"));
        updatedCoupon.setExpirationDate(LocalDateTime.now().plusDays(60));
        updatedCoupon.setMaxUses(200);
        updatedCoupon.setMinPurchaseAmount(new BigDecimal("100.00"));
        updatedCoupon.setActive(false);

        when(couponRepository.findById(1L)).thenReturn(Optional.of(testCoupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(testCoupon);

        Coupon result = couponService.updateCoupon(1L, updatedCoupon);

        assertThat(result).isNotNull();
        assertThat(testCoupon.getCode()).isEqualTo("UPDATED20");
        assertThat(testCoupon.getDiscountType()).isEqualTo("PERCENTAGE");
        verify(couponRepository, times(1)).findById(1L);
        verify(couponRepository, times(1)).save(testCoupon);
    }

    @Test
    void updateCoupon_shouldThrowException_whenCouponNotFound() {
        Coupon updatedCoupon = new Coupon();
        when(couponRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.updateCoupon(999L, updatedCoupon))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Coupon not found with id 999");
    }

    @Test
    void deleteCoupon_shouldDeleteCoupon_whenCouponExists() {
        when(couponRepository.existsById(1L)).thenReturn(true);
        doNothing().when(couponRepository).deleteById(1L);

        couponService.deleteCoupon(1L);

        verify(couponRepository, times(1)).existsById(1L);
        verify(couponRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCoupon_shouldThrowException_whenCouponNotFound() {
        when(couponRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> couponService.deleteCoupon(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Coupon not found with id 999");

        verify(couponRepository, times(1)).existsById(999L);
        verify(couponRepository, never()).deleteById(any());
    }
}
