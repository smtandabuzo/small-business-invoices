package com.sazimtandabuzo.smallbusinessinvoices.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private LocalDateTime paymentDate;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    public Payment() {
        // Default constructor
    }
    
    public Payment(Invoice invoice, BigDecimal amount, PaymentMethod paymentMethod, String notes) {
        this.invoice = invoice;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.notes = notes;
        this.paymentDate = LocalDateTime.now();
    }
}

