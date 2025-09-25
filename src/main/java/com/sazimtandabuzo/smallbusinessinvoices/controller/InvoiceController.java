package com.sazimtandabuzo.smallbusinessinvoices.controller;

import com.sazimtandabuzo.smallbusinessinvoices.dto.InvoiceDTO;
import com.sazimtandabuzo.smallbusinessinvoices.dto.InvoiceRequest;
import com.sazimtandabuzo.smallbusinessinvoices.model.PaymentStatus;
import com.sazimtandabuzo.smallbusinessinvoices.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/invoices")
@CrossOrigin(origins = "http://localhost:4200")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public List<InvoiceDTO> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }
    
    @PostMapping
    public ResponseEntity<InvoiceDTO> createInvoice(@Valid @RequestBody InvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.createInvoice(request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<InvoiceDTO> updateInvoice(
            @PathVariable Long id, 
            @Valid @RequestBody InvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.updateInvoice(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/status/{status}")
    public List<InvoiceDTO> getInvoicesByStatus(@PathVariable PaymentStatus status) {
        return invoiceService.getInvoicesByStatus(status);
    }
    
    @GetMapping("/overdue")
    public List<InvoiceDTO> getOverdueInvoices() {
        return invoiceService.getOverdueInvoices();
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<InvoiceDTO> updateInvoiceStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {
        
        PaymentStatus newStatus = PaymentStatus.valueOf(statusUpdate.get("status"));
        invoiceService.updateInvoiceStatus(id, newStatus);
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }
    
    @GetMapping("/total-outstanding")
    public ResponseEntity<Map<String, BigDecimal>> getTotalOutstanding() {
        BigDecimal total = invoiceService.getTotalOutstanding();
        return ResponseEntity.ok(Collections.singletonMap("totalOutstanding", total));
    }
}
