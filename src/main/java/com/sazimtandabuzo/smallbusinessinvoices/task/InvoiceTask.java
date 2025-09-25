package com.sazimtandabuzo.smallbusinessinvoices.task;

import com.sazimtandabuzo.smallbusinessinvoices.model.Invoice;
import com.sazimtandabuzo.smallbusinessinvoices.model.PaymentStatus;
import com.sazimtandabuzo.smallbusinessinvoices.repository.InvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class InvoiceTask {

    private static final Logger log = LoggerFactory.getLogger(InvoiceTask.class);

    private final InvoiceRepository invoiceRepository;

    @Autowired
    public InvoiceTask(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    // Run every day at 1 AM
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void updateOverdueInvoices() {
        log.info("Starting overdue invoices check...");
        
        LocalDate today = LocalDate.now();
        List<Invoice> overdueInvoices = invoiceRepository
                .findByDueDateBeforeAndStatusNot(today, PaymentStatus.PAID);
        
        int count = 0;
        for (Invoice invoice : overdueInvoices) {
            if (invoice.getStatus() != PaymentStatus.OVERDUE && 
                invoice.getStatus() != PaymentStatus.CANCELLED) {
                invoice.setStatus(PaymentStatus.OVERDUE);
                invoiceRepository.save(invoice);
                count++;
            }
        }
        
        log.info("Updated {} invoices to OVERDUE status", count);
    }
}
