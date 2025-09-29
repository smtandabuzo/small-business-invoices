package com.sazimtandabuzo.smallbusinessinvoices.controller;

import com.sazimtandabuzo.smallbusinessinvoices.dto.InvoiceDTO;
import com.sazimtandabuzo.smallbusinessinvoices.dto.InvoiceRequest;
import com.sazimtandabuzo.smallbusinessinvoices.model.PaymentStatus;
import com.sazimtandabuzo.smallbusinessinvoices.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Invoices", description = "API for managing invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Operation(summary = "Get all invoices", description = "Retrieves a list of all invoices in the system")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of invoices",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = InvoiceDTO.class, type = "array")))
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<InvoiceDTO> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }
    
    @Operation(summary = "Get invoice by ID", description = "Retrieves a specific invoice by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the invoice",
                content = @Content(schema = @Schema(implementation = InvoiceDTO.class))),
        @ApiResponse(responseCode = "404", description = "Invoice not found",
                content = @Content)
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InvoiceDTO> getInvoiceById(
            @Parameter(description = "ID of the invoice to be retrieved", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }
    
    @Operation(summary = "Create a new invoice", description = "Creates a new invoice with the provided details")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully created the invoice",
                content = @Content(schema = @Schema(implementation = InvoiceDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input",
                content = @Content)
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InvoiceDTO> createInvoice(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Invoice details to be created",
                    required = true,
                    content = @Content(schema = @Schema(implementation = InvoiceRequest.class)))
            @Valid @RequestBody InvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.createInvoice(request));
    }
    
    @Operation(summary = "Update an existing invoice", description = "Updates an existing invoice with the provided details")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully updated the invoice",
                content = @Content(schema = @Schema(implementation = InvoiceDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input",
                content = @Content),
        @ApiResponse(responseCode = "404", description = "Invoice not found",
                content = @Content)
    })
    @PutMapping(value = "/{id}", 
            consumes = MediaType.APPLICATION_JSON_VALUE, 
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InvoiceDTO> updateInvoice(
            @Parameter(description = "ID of the invoice to be updated", required = true)
            @PathVariable Long id, 
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated invoice details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = InvoiceRequest.class)))
            @Valid @RequestBody InvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.updateInvoice(id, request));
    }
    
    @Operation(summary = "Delete an invoice", description = "Deletes an invoice by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Successfully deleted the invoice"),
        @ApiResponse(responseCode = "404", description = "Invoice not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(
            @Parameter(description = "ID of the invoice to be deleted", required = true)
            @PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Get invoices by status", description = "Retrieves all invoices with a specific status")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved invoices by status",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = InvoiceDTO.class, type = "array")))
    @GetMapping(value = "/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<InvoiceDTO> getInvoicesByStatus(
            @Parameter(description = "Status of the invoices to be retrieved", required = true)
            @PathVariable PaymentStatus status) {
        return invoiceService.getInvoicesByStatus(status);
    }
    
    @Operation(summary = "Get overdue invoices", description = "Retrieves all invoices that are past their due date and not fully paid")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved overdue invoices",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = InvoiceDTO.class, type = "array")))
    @GetMapping(value = "/overdue", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<InvoiceDTO> getOverdueInvoices() {
        return invoiceService.getOverdueInvoices();
    }
    
    @Operation(summary = "Update invoice status", description = "Updates the status of a specific invoice")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully updated the invoice status",
                content = @Content(schema = @Schema(implementation = InvoiceDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid status value",
                content = @Content),
        @ApiResponse(responseCode = "404", description = "Invoice not found",
                content = @Content)
    })
    @PatchMapping(value = "/{id}/status", 
            consumes = MediaType.APPLICATION_JSON_VALUE, 
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InvoiceDTO> updateInvoiceStatus(
            @Parameter(description = "ID of the invoice to update status", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Status update request",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Map.class, example = "{\"status\": \"PAID\"}")
                    ))
            @RequestBody Map<String, String> statusUpdate) {
        
        PaymentStatus newStatus = PaymentStatus.valueOf(statusUpdate.get("status"));
        invoiceService.updateInvoiceStatus(id, newStatus);
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }
    
    @GetMapping("/total-outstanding")
    public ResponseEntity<Map<String, BigDecimal>> getTotalOutstanding() {
        BigDecimal total = invoiceService.getTotalOutstanding();
        return ResponseEntity.ok(Collections.singletonMap("totalOutstanding", total));
    }
}
