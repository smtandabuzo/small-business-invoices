package com.sazimtandabuzo.smallbusinessinvoices.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    
    @NotBlank(message = "Invoice number is required")
    @Size(max = 50, message = "Invoice number cannot exceed 50 characters")
    @Column(nullable = false, unique = true)
    private String invoiceNumber;
    
    @NotBlank(message = "Customer name is required")
    @Pattern(regexp = "^[a-zA-Z0-9\s.,'-]+$", message = "Customer name contains invalid characters")
    @Size(min = 2, max = 100, message = "Customer name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String customerName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must be at most 50 characters")
    @Column(name = "customer_email", nullable = false)
    private String customerEmail;
    
    @NotNull(message = "Issue date is required")
    @Column(nullable = false)
    private LocalDate issueDate;
    
    @NotNull(message = "Due date is required")
    @FutureOrPresent(message = "Due date must be today or in the future")
    @Column(nullable = false)
    private LocalDate dueDate;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount must have up to 10 digits before and 2 after decimal")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @DecimalMin(value = "0.00", message = "Amount paid cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Amount paid must have up to 10 digits before and 2 after decimal")
    @Column(name = "amount_paid", precision = 10, scale = 2)
    private BigDecimal amountPaid = BigDecimal.ZERO;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    
    @Size(max = 1000, message = "Description cannot exceed 100 characters")
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
