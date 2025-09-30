// src/main/java/com/yourcompany/smallbusinessinvoices/exception/RateLimitExceededException.java
package com.sazimtandabuzo.smallbusinessinvoices.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String message) {
        super(message);
    }
}