package com.sazimtandabuzo.smallbusinessinvoices.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InvoiceRequest {
    @NotBlank(message = "Customer name is required")
    @Pattern(regexp = "^[a-zA-Z0-9\s.,'-]+$", message = "Customer name contains invalid characters")
    @Size(min = 2, max = 100, message = "Customer name must be between 2 and 100 characters")
    private String customerName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must be at most 100 characters")
    private String customerEmail;
    
    @NotNull(message = "Issue date is required")
    private LocalDate issueDate;
    
    @NotNull(message = "Due date is required")
    @FutureOrPresent(message = "Due date must be today or in the future")
    private LocalDate dueDate;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount must have up to 10 digits before and 2 after decimal")
    private BigDecimal amount;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(PENDING|PAID|OVERDUE|CANCELLED)$", message = "Status must be one of: PENDING, PAID, OVERDUE, CANCELLED")
    private String status;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;
}
