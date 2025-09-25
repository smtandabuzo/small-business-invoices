package com.sazimtandabuzo.smallbusinessinvoices.controller;

import com.sazimtandabuzo.smallbusinessinvoices.dto.PaymentDTO;
import com.sazimtandabuzo.smallbusinessinvoices.dto.PaymentRequest;
import com.sazimtandabuzo.smallbusinessinvoices.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/payments")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/invoice/{invoiceId}")
    public List<PaymentDTO> getPaymentsByInvoice(@PathVariable Long invoiceId) {
        return paymentService.getPaymentsByInvoice(invoiceId);
    }
    
    @PostMapping
    public ResponseEntity<PaymentDTO> recordPayment(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.recordPayment(request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
