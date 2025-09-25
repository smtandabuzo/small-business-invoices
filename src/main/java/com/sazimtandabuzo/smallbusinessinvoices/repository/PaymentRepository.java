package com.sazimtandabuzo.smallbusinessinvoices.repository;

import com.sazimtandabuzo.smallbusinessinvoices.model.Invoice;
import com.sazimtandabuzo.smallbusinessinvoices.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByInvoice(Invoice invoice);
    List<Payment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Payment> findByInvoiceId(Long invoiceId);
    void deleteByInvoiceId(Long invoiceId);
}
