package com.sazimtandabuzo.smallbusinessinvoices.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InvoiceRequest {
    private String customerName;
    private String customerEmail;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private BigDecimal amount;
    private String description;
}
