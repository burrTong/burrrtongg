package com.example.backend.service;

import com.example.backend.entity.Order;
import com.example.backend.entity.OrderItem;
import com.example.backend.repository.OrderRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color; // Added import for java.awt.Color
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class ReportService {

    private final OrderRepository orderRepository;

    public ReportService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public byte[] generateOrderPdf(Long orderId) throws IOException, DocumentException {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            throw new IllegalArgumentException("Order not found with ID: " + orderId);
        }
        Order order = orderOptional.get();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, new Color(0, 0, 0)); // Replaced BaseColor.BLACK
            Paragraph title = new Paragraph("Order Summary", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Order Details
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(0, 0, 0)); // Replaced BaseColor.BLACK
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, new Color(0, 0, 0)); // Replaced BaseColor.BLACK

            document.add(new Paragraph("Order ID: " + order.getId(), normalFont));
            document.add(new Paragraph("Order Date: " + order.getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), normalFont));
            document.add(new Paragraph("Customer: " + order.getCustomer().getUsername(), normalFont));
            document.add(new Paragraph("Status: " + order.getStatus(), normalFont));
            document.add(new Paragraph("\n"));

            // Order Items Table
            document.add(new Paragraph("Order Items:", headerFont));
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Table Headers
            String[] headers = {"Product Name", "Quantity", "Price", "Subtotal"};
            for (String header : headers) {
                PdfPCell hCell = new PdfPCell(new Phrase(header, headerFont));
                hCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hCell.setBackgroundColor(new Color(200, 200, 200)); // Replaced BaseColor.LIGHT_GRAY
                table.addCell(hCell);
            }

            // Table Rows
            for (OrderItem item : order.getOrderItems()) {
                table.addCell(new Phrase(item.getProduct().getName(), normalFont));
                table.addCell(new Phrase(String.valueOf(item.getQuantity()), normalFont));
                table.addCell(new Phrase(String.format("%.2f", item.getPrice()), normalFont));
                table.addCell(new Phrase(String.format("%.2f", item.getPrice() * item.getQuantity()), normalFont));
            }
            document.add(table);

            // Totals
            document.add(new Paragraph("Subtotal: " + String.format("%.2f", order.getOrderItems().stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum()), headerFont));
            if (order.getCoupon() != null) {
                document.add(new Paragraph("Coupon Discount (" + order.getCoupon().getCode() + "): -" + String.format("%.2f", order.getOrderItems().stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum() - order.getTotalPrice()), headerFont));
            }
            document.add(new Paragraph("Total Price: " + String.format("%.2f", order.getTotalPrice()), headerFont));

            document.close();
        } catch (DocumentException e) {
            throw new IOException("Error generating PDF document", e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
        return baos.toByteArray();
    }
}
