package com.sazimtandabuzo.smallbusinessinvoices.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String invoiceNumber;
    
    @Column(nullable = false)
    private String customerName;
    
    @Column(nullable = false)
    private String customerEmail;
    
    @Column(nullable = false)
    private LocalDate issueDate;
    
    @Column(nullable = false)
    private LocalDate dueDate;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "amount_paid", precision = 10, scale = 2)
    private BigDecimal amountPaid = BigDecimal.ZERO;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = this.dueDate.isBefore(LocalDate.now()) ? 
                PaymentStatus.OVERDUE : PaymentStatus.PENDING;
        }
        if (this.amountPaid == null) {
            this.amountPaid = BigDecimal.ZERO;
        }
        
        // Update status based on payment
        updateStatusBasedOnPayment();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateStatusBasedOnPayment();
    }
    
    private void updateStatusBasedOnPayment() {
        if (this.amountPaid == null) {
            this.amountPaid = BigDecimal.ZERO;
        }
        
        if (this.amountPaid.compareTo(BigDecimal.ZERO) == 0) {
            this.status = this.dueDate.isBefore(LocalDate.now()) ? 
                PaymentStatus.OVERDUE : PaymentStatus.PENDING;
        } else if (this.amountPaid.compareTo(this.amount) >= 0) {
            this.status = PaymentStatus.PAID;
        } else {
            this.status = PaymentStatus.PARTIALLY_PAID;
        }
    }
}
