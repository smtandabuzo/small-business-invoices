package com.sazimtandabuzo.smallbusinessinvoices.controller;

import com.sazimtandabuzo.smallbusinessinvoices.dto.PaymentDTO;
import com.sazimtandabuzo.smallbusinessinvoices.dto.PaymentRequest;
import com.sazimtandabuzo.smallbusinessinvoices.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing payment operations.
 * Provides endpoints for recording, retrieving, and deleting payments.
 */
@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Payments", description = "APIs for managing invoice payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(
        summary = "Get payments by invoice ID",
        description = "Retrieves all payments associated with a specific invoice"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved payments",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = @ArraySchema(schema = @Schema(implementation = PaymentDTO.class)),
                examples = @ExampleObject(
                    value = "[{\"id\": 1, \"amount\": 150.00, \"paymentDate\": \"2023-10-15T14:30:00\", " +
                            "\"paymentMethod\": \"CREDIT_CARD\", \"notes\": " +
                            "\"Paid via Stripe\", \"invoiceId\": 1}]"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Invoice not found",
            content = @Content
        )
    })
    @GetMapping(
        value = "/invoice/{invoiceId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<PaymentDTO> getPaymentsByInvoice(
            @Parameter(
                description = "ID of the invoice to retrieve payments for",
                required = true,
                example = "1"
            )
            @PathVariable Long invoiceId) {
        return paymentService.getPaymentsByInvoice(invoiceId);
    }
    
    @Operation(
        summary = "Record a new payment",
        description = "Records a new payment for an invoice"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Payment successfully recorded",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = PaymentDTO.class),
                examples = @ExampleObject(
                    name = "recordPaymentResponse",
                    value = "{\"id\": 1, \"amount\": 150.00, \"paymentDate\": \"2023-10-15T14:30:00\", " +
                            "\"paymentMethod\": \"CREDIT_CARD\", \"notes\": " +
                            "\"Paid via Stripe\", \"invoiceId\": 1}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(
                examples = @ExampleObject(
                    name = "validationError",
                    value = "{\"timestamp\": \"2023-10-15T14:32:15.12345\", " +
                            "\"status\": 400, " +
                            "\"error\": \"Bad Request\", " +
                            "\"message\": \"Validation failed\", " +
                            "\"path\": \"/api/payments\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Invoice not found",
            content = @Content
        )
    })
    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PaymentDTO> recordPayment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Payment details to record",
                required = true,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PaymentRequest.class),
                    examples = @ExampleObject(
                        name = "recordPaymentRequest",
                        value = "{\"invoiceId\": 1, \"amount\": 150.00, " +
                                "\"paymentMethod\": \"CREDIT_CARD\", " +
                                "\"paymentDate\": \"2023-10-15T14:30:00\", " +
                                "\"notes\": \"Paid via Stripe\"}"
                    )
                )
            )
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.recordPayment(request));
    }
    
    @Operation(
        summary = "Delete a payment",
        description = "Deletes a payment by its ID and updates the associated invoice status if needed"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Payment successfully deleted"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Payment not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid payment ID",
            content = @Content
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(
            @Parameter(
                description = "ID of the payment to delete",
                required = true,
                example = "1"
            )
            @PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
