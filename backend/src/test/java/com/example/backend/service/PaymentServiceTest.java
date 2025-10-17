package com.example.backend.service;

import com.example.backend.entity.Order;
import com.example.backend.entity.Payment;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.PaymentStatus;
import com.example.backend.model.dto.PaymentRequest;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Payment payment1;
    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setTotalPrice(100.0);

        payment1 = new Payment();
        payment1.setId(1L);
        payment1.setOrder(order);
        payment1.setAmount(100.0);
        payment1.setPaymentDate(LocalDateTime.now());
        payment1.setStatus(PaymentStatus.COMPLETED);
    }

    @Test
    void getPaymentById_shouldReturnPayment_whenFound() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment1));

        Payment foundPayment = paymentService.getPaymentById(1L);

        assertNotNull(foundPayment);
        assertEquals(payment1.getAmount(), foundPayment.getAmount());
        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    void getPaymentById_shouldThrowResourceNotFoundException_whenNotFound() {
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.getPaymentById(1L));
        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    void createPayment_shouldCreatePayment() {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment savedPayment = invocation.getArgument(0);
            savedPayment.setId(3L);
            return savedPayment;
        });

        Payment createdPayment = paymentService.createPayment(paymentRequest);

        assertNotNull(createdPayment);
        assertEquals(order.getTotalPrice(), createdPayment.getAmount());
        assertEquals(PaymentStatus.COMPLETED, createdPayment.getStatus());
        assertNotNull(createdPayment.getPaymentDate());
        verify(orderRepository, times(1)).findById(1L);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void createPayment_shouldThrowResourceNotFoundException_whenOrderNotFound() {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(99L);

        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.createPayment(paymentRequest));
        verify(orderRepository, times(1)).findById(99L);
        verify(paymentRepository, never()).save(any(Payment.class));
    }
}
