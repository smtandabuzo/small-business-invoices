package com.sazimtandabuzo.smallbusinessinvoices.repository;

import com.sazimtandabuzo.smallbusinessinvoices.model.Invoice;
import com.sazimtandabuzo.smallbusinessinvoices.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByCustomerNameContainingIgnoreCase(String customerName);
    List<Invoice> findByStatus(PaymentStatus status);
    List<Invoice> findByIssueDateBetween(LocalDate startDate, LocalDate endDate);
    List<Invoice> findByDueDateBeforeAndStatusNot(LocalDate date, PaymentStatus status);
    boolean existsByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByStatusNot(PaymentStatus status);
}
