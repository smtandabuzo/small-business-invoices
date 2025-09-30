package com.sazimtandabuzo.smallbusinessinvoices.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    
    // Handle validation errors for @Valid annotated objects
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", false);
        response.put("message", "Validation failed");
        response.put("status", status.value());
        
        // Group errors by field
        Map<String, List<String>> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                    FieldError::getField,
                    Collectors.mapping(
                        fieldError -> fieldError.getDefaultMessage() != null ? 
                            fieldError.getDefaultMessage() : "Invalid value",
                        Collectors.toList()
                    )
                ));
        
        // If there's only one error, include it in the main response
        if (fieldErrors.size() == 1) {
            Map.Entry<String, List<String>> entry = fieldErrors.entrySet().iterator().next();
            response.put("field", entry.getKey());
            response.put("error", entry.getValue().get(0));
        } else {
            response.put("errors", fieldErrors);
        }
        
        return new ResponseEntity<>(response, headers, status);
    }
    
    // Handle malformed JSON requests
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Malformed JSON request");
        body.put("message", ex.getMostSpecificCause().getMessage());
        
        return new ResponseEntity<>(body, headers, status);
    }
    
    // Handle constraint violations (e.g., @Validated on service layer)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Error");
        body.put("message", ex.getMessage());
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    
    // Handle type mismatch in request parameters
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Invalid Parameter");
        body.put("message", String.format(
            "The parameter '%s' of value '%s' could not be converted to type '%s'",
            ex.getName(), 
            ex.getValue(), 
            ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"
        ));
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    
    // Handle resource not found exceptions
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<Object> handleInvalidDataAccessApiUsageException(
            InvalidDataAccessApiUsageException ex, WebRequest request) {
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Invalid Request");
        
        // Extract a more user-friendly message
        String message = ex.getMostSpecificCause().getMessage();
        if (message != null && message.contains("The given id must not be null")) {
            message = "ID parameter is required and cannot be null";
        } else if (message != null) {
            // Generalize other similar errors
            message = message.replace("org.springframework.", "")
                           .replace("java.lang.", "");
        } else {
            message = "Invalid request parameters";
        }
        
        body.put("message", message);
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    
    // Handle invalid payment operations
    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<Object> handleInvalidPaymentException(
            InvalidPaymentException ex, WebRequest request) {
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    
    // Handle illegal argument exceptions
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Object> handleRateLimitExceeded(
            RateLimitExceededException ex, HttpServletRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
        body.put("error", "Too Many Requests");
        body.put("message", ex.getMessage());
        body.put("path", request.getRequestURI());

        return new ResponseEntity<>(body, HttpStatus.TOO_MANY_REQUESTS);
    }
    
    // Generic exception handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "An unexpected error occurred: " + ex.getMessage());
        
        // Log the full exception for debugging
        logger.error("Unhandled exception occurred: ", ex);
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
