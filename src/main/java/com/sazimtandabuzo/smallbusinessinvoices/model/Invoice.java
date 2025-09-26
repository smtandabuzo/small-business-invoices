package com.sazimtandabuzo.smallbusinessinvoices.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Data
@Entity
@Table(name = "invoices")
@NoArgsConstructor
@AllArgsConstructor
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
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted")
    private boolean deleted = false;
    
    public Invoice(String customerName, String customerEmail, LocalDate issueDate, 
                  LocalDate dueDate, BigDecimal amount, String description) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.amount = amount != null ? amount : BigDecimal.ZERO;
        this.description = description;
        this.amountPaid = BigDecimal.ZERO;
        this.status = dueDate.isBefore(LocalDate.now()) ? PaymentStatus.OVERDUE : PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.deleted = false;
    }
    
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
