package com.example.backend.controller;

import com.example.backend.config.TestConfig;
import com.example.backend.entity.Coupon;
import com.example.backend.service.CouponService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CouponService couponService;

    private Coupon testCoupon;

    @BeforeEach
    void setUp() {
        testCoupon = new Coupon();
        testCoupon.setId(1L);
        testCoupon.setCode("SAVE10");
        testCoupon.setDiscountValue(BigDecimal.valueOf(10));
        testCoupon.setActive(true);
    }

    @Test
    void getAllCoupons_shouldReturnListOfCoupons() throws Exception {
        Coupon coupon2 = new Coupon();
        coupon2.setId(2L);
        coupon2.setCode("SAVE20");
        coupon2.setDiscountValue(BigDecimal.valueOf(20));

        List<Coupon> coupons = Arrays.asList(testCoupon, coupon2);
        when(couponService.getAllCoupons()).thenReturn(coupons);

        mockMvc.perform(get("/api/coupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].code").value("SAVE10"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].code").value("SAVE20"));
    }

    @Test
    void getActiveCoupons_shouldReturnActiveCoupons() throws Exception {
        List<Coupon> activeCoupons = Arrays.asList(testCoupon);
        when(couponService.getActiveCoupons()).thenReturn(activeCoupons);

        mockMvc.perform(get("/api/coupons/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].isActive").value(true));
    }

    @Test
    void createCoupon_shouldReturnCreatedCoupon() throws Exception {
        Coupon newCoupon = new Coupon();
        newCoupon.setCode("NEW10");
        newCoupon.setDiscountValue(BigDecimal.valueOf(10));

        when(couponService.createCoupon(any(Coupon.class))).thenReturn(testCoupon);

        mockMvc.perform(post("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCoupon)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("SAVE10"));
    }

    @Test
    void getCouponByCode_shouldReturnCoupon() throws Exception {
        when(couponService.getCouponByCode("SAVE10")).thenReturn(testCoupon);

        mockMvc.perform(get("/api/coupons/SAVE10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SAVE10"))
                .andExpect(jsonPath("$.discountValue").value(10));
    }

    @Test
    void updateCoupon_shouldReturnUpdatedCoupon() throws Exception {
        Coupon updatedCoupon = new Coupon();
        updatedCoupon.setCode("SAVE15");
        updatedCoupon.setDiscountValue(BigDecimal.valueOf(15));

        when(couponService.updateCoupon(any(Long.class), any(Coupon.class))).thenReturn(updatedCoupon);

        mockMvc.perform(put("/api/coupons/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCoupon)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SAVE15"));
    }

    @Test
    void deleteCoupon_shouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(delete("/api/coupons/1"))
                .andExpect(status().isOk());
    }
}
