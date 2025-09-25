package com.sazimtandabuzo.smallbusinessinvoices.dto;

import com.sazimtandabuzo.smallbusinessinvoices.model.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InvoiceDTO {
    private Long id;
    private String invoiceNumber;
    private String customerName;
    private String customerEmail;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private BigDecimal amount;
    private PaymentStatus status;
    private String description;
    private BigDecimal amountPaid;
    private BigDecimal balance;
}

