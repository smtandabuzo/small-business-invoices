package com.sazimtandabuzo.smallbusinessinvoices.dto;

import com.sazimtandabuzo.smallbusinessinvoices.model.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Long id;
    private Long invoiceId;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private PaymentMethod paymentMethod;
    private String notes;
}

