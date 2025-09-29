package com.sazimtandabuzo.smallbusinessinvoices.dto;

import com.sazimtandabuzo.smallbusinessinvoices.model.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentRequest {
    @NotNull(message = "Invoice ID is required")
    @Positive(message = "Invoice ID must be a positive number")
    private Long invoiceId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount must have up to 10 digits before and 2 after decimal")
    private BigDecimal amount;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
    
    @NotNull(message = "Payment date is required")
    private LocalDateTime paymentDate;
    
    @Size(max = 100, message = "Notes cannot exceed 100 characters")
    private String notes;
}
