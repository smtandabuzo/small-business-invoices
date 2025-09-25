package com.sazimtandabuzo.smallbusinessinvoices.service;

import com.sazimtandabuzo.smallbusinessinvoices.dto.InvoiceDTO;
import com.sazimtandabuzo.smallbusinessinvoices.dto.InvoiceRequest;
import com.sazimtandabuzo.smallbusinessinvoices.exception.ResourceNotFoundException;
import com.sazimtandabuzo.smallbusinessinvoices.model.Invoice;
import com.sazimtandabuzo.smallbusinessinvoices.model.PaymentStatus;
import com.sazimtandabuzo.smallbusinessinvoices.repository.InvoiceRepository;
import com.sazimtandabuzo.smallbusinessinvoices.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;
    
    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(this::convertToDtoWithPayments)
                .collect(Collectors.toList());
    }
    
    public InvoiceDTO getInvoiceById(Long id) {
        Invoice invoice = getInvoiceOrThrow(id);
        return convertToDtoWithPayments(invoice);
    }
    
    @Transactional
    public InvoiceDTO createInvoice(InvoiceRequest request) {
        // Validate required fields
        if (request.getCustomerName() == null || request.getCustomerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        if (request.getCustomerEmail() == null || request.getCustomerEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer email is required");
        }
        if (request.getDueDate() == null) {
            throw new IllegalArgumentException("Due date is required");
        }
        
        // Set default amount to zero if not provided
        BigDecimal amount = request.getAmount() != null ? request.getAmount() : BigDecimal.ZERO;
        
        // Generate a unique invoice number
        String invoiceNumber = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Create new invoice
        Invoice invoice = new Invoice(
            request.getCustomerName(),
            request.getCustomerEmail(),
            request.getIssueDate() != null ? request.getIssueDate() : LocalDate.now(),
            request.getDueDate(),
            amount,
            request.getDescription() != null ? request.getDescription() : ""
        );
        
        invoice.setInvoiceNumber(invoiceNumber);
        
        Invoice savedInvoice = invoiceRepository.save(invoice);
        return convertToDto(savedInvoice);
    }
    
    @Transactional
    public InvoiceDTO updateInvoice(Long id, InvoiceRequest request) {
        Invoice invoice = getInvoiceOrThrow(id);
        
        // Update invoice fields
        invoice.setCustomerName(request.getCustomerName());
        invoice.setCustomerEmail(request.getCustomerEmail());
        invoice.setIssueDate(request.getIssueDate());
        invoice.setDueDate(request.getDueDate());
        invoice.setAmount(request.getAmount());
        invoice.setDescription(request.getDescription());
        
        // Update status if needed
        updateInvoiceStatusBasedOnPayments(invoice);
        
        return convertToDto(invoiceRepository.save(invoice));
    }
    
    @Transactional
    public void deleteInvoice(Long id) {
        Invoice invoice = getInvoiceOrThrow(id);
        
        // Delete all payments for this invoice first
        paymentRepository.deleteByInvoiceId(id);
        
        // Then delete the invoice
        invoiceRepository.delete(invoice);
    }
    
    @Transactional
    public void updateInvoiceStatus(Long id, PaymentStatus status) {
        Invoice invoice = getInvoiceOrThrow(id);
        
        // Only allow certain status transitions
        if (invoice.getStatus() != PaymentStatus.CANCELLED || status != PaymentStatus.CANCELLED) {
            invoice.setStatus(status);
            invoiceRepository.save(invoice);
        }
    }
    
    public List<InvoiceDTO> getInvoicesByStatus(PaymentStatus status) {
        return invoiceRepository.findByStatus(status).stream()
                .map(this::convertToDtoWithPayments)
                .collect(Collectors.toList());
    }
    
    public List<InvoiceDTO> getOverdueInvoices() {
        return invoiceRepository.findByDueDateBeforeAndStatusNot(
                LocalDate.now(), 
                PaymentStatus.PAID
            ).stream()
            .map(this::convertToDtoWithPayments)
            .collect(Collectors.toList());
    }
    
    public BigDecimal getTotalOutstanding() {
        return invoiceRepository.findByStatusNot(PaymentStatus.PAID).stream()
                .map(invoice -> {
                    BigDecimal paidAmount = paymentRepository.findByInvoiceId(invoice.getId()).stream()
                            .map(payment -> payment.getAmount())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return invoice.getAmount().subtract(paidAmount);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private Invoice getInvoiceOrThrow(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
    }
    
    private void updateInvoiceStatusBasedOnPayments(Invoice invoice) {
        if (invoice.getStatus() == PaymentStatus.PAID || invoice.getStatus() == PaymentStatus.CANCELLED) {
            return; // No need to update status if already paid or cancelled
        }
        
        BigDecimal totalPaid = paymentRepository.findByInvoiceId(invoice.getId()).stream()
                .map(payment -> payment.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalPaid.compareTo(BigDecimal.ZERO) == 0) {
            // No payments made yet
            invoice.setStatus(invoice.getDueDate().isBefore(LocalDate.now()) ? 
                    PaymentStatus.OVERDUE : PaymentStatus.PENDING);
        } else if (totalPaid.compareTo(invoice.getAmount()) >= 0) {
            // Fully paid
            invoice.setStatus(PaymentStatus.PAID);
        } else {
            // Partially paid
            invoice.setStatus(PaymentStatus.PARTIALLY_PAID);
        }
    }
    
    private InvoiceDTO convertToDto(Invoice invoice) {
        return modelMapper.map(invoice, InvoiceDTO.class);
    }
    
    private InvoiceDTO convertToDtoWithPayments(Invoice invoice) {
        InvoiceDTO dto = convertToDto(invoice);
        
        // Calculate amount paid and balance
        BigDecimal paidAmount = paymentRepository.findByInvoiceId(invoice.getId()).stream()
                .map(payment -> payment.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        dto.setAmountPaid(paidAmount);
        dto.setBalance(invoice.getAmount().subtract(paidAmount));
        
        return dto;
    }
}
