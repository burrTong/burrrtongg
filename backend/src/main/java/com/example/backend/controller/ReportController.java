package com.example.backend.controller;

import com.example.backend.service.ReportService;
import com.example.backend.service.OrderService;
import com.lowagie.text.DocumentException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:5173")
public class ReportController {

    private final ReportService reportService;
    private final OrderService orderService;

    public ReportController(ReportService reportService, OrderService orderService) {
        this.reportService = reportService;
        this.orderService = orderService;
    }

    @GetMapping("/orders/{orderId}/pdf")
    @PreAuthorize("hasRole('CUSTOMER') and @orderService.isOwner(#orderId, authentication.principal.username)")
    public ResponseEntity<byte[]> getOrderPdf(@PathVariable Long orderId, Principal principal) throws IOException, DocumentException {
        byte[] pdfBytes = reportService.generateOrderPdf(orderId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "order_" + orderId + ".pdf";
        headers.setContentDispositionFormData("attachment", filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
