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
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    public Invoice() {
        // Default constructor
    }
    
    // Custom constructor for creating new invoices
    public Invoice(String customerName, String customerEmail, LocalDate issueDate, 
                  LocalDate dueDate, BigDecimal amount, String description) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.amount = amount;
        this.description = description;
        
        // Set status based on due date
        if (dueDate.isBefore(LocalDate.now())) {
            this.status = PaymentStatus.OVERDUE;
        }
    }
}
