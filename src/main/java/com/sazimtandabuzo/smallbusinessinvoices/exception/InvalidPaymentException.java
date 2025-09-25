package com.sazimtandabuzo.smallbusinessinvoices.exception;

public class InvalidPaymentException extends RuntimeException {
    public InvalidPaymentException(String message) {
        super(message);
    }
}
