package com.sazimtandabuzo.smallbusinessinvoices.dto;

import com.sazimtandabuzo.smallbusinessinvoices.model.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private Long invoiceId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String notes;
}
