package com.sazimtandabuzo.smallbusinessinvoices.service;

import com.sazimtandabuzo.smallbusinessinvoices.dto.PaymentDTO;
import com.sazimtandabuzo.smallbusinessinvoices.dto.PaymentRequest;
import com.sazimtandabuzo.smallbusinessinvoices.exception.InvalidPaymentException;
import com.sazimtandabuzo.smallbusinessinvoices.exception.ResourceNotFoundException;
import com.sazimtandabuzo.smallbusinessinvoices.model.Invoice;
import com.sazimtandabuzo.smallbusinessinvoices.model.Payment;
import com.sazimtandabuzo.smallbusinessinvoices.model.PaymentMethod;
import com.sazimtandabuzo.smallbusinessinvoices.model.PaymentStatus;
import com.sazimtandabuzo.smallbusinessinvoices.repository.InvoiceRepository;
import com.sazimtandabuzo.smallbusinessinvoices.repository.PaymentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;
    private final ModelMapper modelMapper;
    
    public List<PaymentDTO> getPaymentsByInvoice(Long invoiceId) {
        // Verify invoice exists
        getInvoiceOrThrow(invoiceId);
        
        return paymentRepository.findByInvoiceId(invoiceId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public PaymentDTO recordPayment(@Valid PaymentRequest request) {
        // Get the invoice - will throw ResourceNotFoundException if not found
        Invoice invoice = getInvoiceOrThrow(request.getInvoiceId());
        
        // Check if invoice is already fully paid or cancelled
        if (invoice.getStatus() == PaymentStatus.PAID) {
            throw new InvalidPaymentException("Invoice is already fully paid");
        }
        
        if (invoice.getStatus() == PaymentStatus.CANCELLED) {
            throw new InvalidPaymentException("Cannot record payment for a cancelled invoice");
        }
        
        // Calculate remaining balance
        BigDecimal totalPaid = getTotalPaidAmount(invoice);
        BigDecimal remainingAmount = invoice.getAmount().subtract(totalPaid);
        
        // Check if payment exceeds the remaining amount
        if (request.getAmount().compareTo(remainingAmount) > 0) {
            throw new InvalidPaymentException(
                String.format("Payment amount (%.2f) exceeds the remaining invoice amount (%.2f)", 
                    request.getAmount(), remainingAmount)
            );
        }
        
        // Create and save payment
        Payment payment = new Payment(
            invoice,
            request.getAmount(),
            request.getPaymentMethod(),
            request.getNotes()
        );
        
        Payment savedPayment = paymentRepository.save(payment);
        
        // Update invoice status based on the new payment
        updateInvoiceStatus(invoice, totalPaid.add(request.getAmount()));
        
        return convertToDto(savedPayment);
    }
    
    @Transactional
    public void deletePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));
        
        Invoice invoice = payment.getInvoice();
        BigDecimal paymentAmount = payment.getAmount();
        
        // Delete the payment
        paymentRepository.delete(payment);
        
        // Recalculate and update invoice status
        BigDecimal totalPaid = getTotalPaidAmount(invoice);
        updateInvoiceStatus(invoice, totalPaid);
    }
    
    private BigDecimal getTotalPaidAmount(Invoice invoice) {
        return paymentRepository.findByInvoiceId(invoice.getId()).stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Updates the invoice status based on the payment amount and due date
     * @param invoice The invoice to update
     * @param newTotalPaid The new total paid amount including the latest payment
     */
    private void updateInvoiceStatus(Invoice invoice, BigDecimal newTotalPaid) {
        LocalDateTime now = LocalDateTime.now();
        boolean isOverdue = invoice.getDueDate().isBefore(now.toLocalDate());
        
        // Calculate payment status based on amount paid
        int paymentComparison = newTotalPaid.compareTo(BigDecimal.ZERO);
        int fullPaymentComparison = newTotalPaid.compareTo(invoice.getAmount());
        
        PaymentStatus newStatus;
        
        if (paymentComparison == 0) {
            // No payments made yet
            newStatus = isOverdue ? PaymentStatus.OVERDUE : PaymentStatus.PENDING;
        } else if (fullPaymentComparison >= 0) {
            // Fully paid (or overpaid)
            newStatus = PaymentStatus.PAID;
        } else if (paymentComparison > 0) {
            // Partially paid
            newStatus = isOverdue ? PaymentStatus.PARTIALLY_PAID_OVERDUE : PaymentStatus.PARTIALLY_PAID;
        } else {
            // Shouldn't normally happen as payment amount is validated
            newStatus = PaymentStatus.PENDING;
        }
        
        // Only update if status has changed
        if (invoice.getStatus() != newStatus) {
            invoice.setStatus(newStatus);
            invoiceRepository.save(invoice);
            
            log.info("Updated invoice {} status to {} (paid: {}/{})", 
                    invoice.getId(), newStatus, newTotalPaid, invoice.getAmount());
        }
    }
    
    private Invoice getInvoiceOrThrow(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));
    }
    
    private PaymentDTO convertToDto(Payment payment) {
        PaymentDTO dto = modelMapper.map(payment, PaymentDTO.class);
        dto.setInvoiceId(payment.getInvoice().getId());
        return dto;
    }
}
