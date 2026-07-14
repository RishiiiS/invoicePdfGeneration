package com.telecom.invoicePdfGeneration.services.impl;

import com.telecom.invoicePdfGeneration.entity.BillingCycle;
import com.telecom.invoicePdfGeneration.entity.Invoice;
import com.telecom.invoicePdfGeneration.repository.BillingCycleRepository;
import com.telecom.invoicePdfGeneration.repository.InvoiceRepository;
import com.telecom.invoicePdfGeneration.services.BillingCycleSchedulerService;
import com.telecom.invoicePdfGeneration.services.InvoiceProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingCycleSchedulerServiceImpl implements BillingCycleSchedulerService {

    private final BillingCycleRepository billingCycleRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceProcessingService invoiceProcessingService;

    @Override
    public void processTodaysBillingCycle() {
        LocalDate today = LocalDate.now();
        log.info("Today's Date: {}", today);

        Optional<BillingCycle> optionalCycle = billingCycleRepository.findByBillingDate(today);

        if (optionalCycle.isEmpty()) {
            log.info("No billing cycle scheduled for today.");
            return;
        }

        BillingCycle cycle = optionalCycle.get();
        String cycleCode = cycle.getCycleCode();

        log.info("Billing Cycle Found: {}", cycle);
        processBillingCycle(cycleCode);
    }

    @Override
    public void processBillingCycle(String cycleCode) {
        log.info("Billing Cycle Code: {}", cycleCode);

        List<Invoice> pendingInvoices = findPendingInvoices(cycleCode);
        log.info("Pending Invoice Count: {}", pendingInvoices.size());

        for (Invoice invoice : pendingInvoices) {
            processSingleInvoice(invoice.getInvoiceId());
        }
    }

    private List<Invoice> findPendingInvoices(String cycleCode) {
        return invoiceRepository.findByBillingCycleId(cycleCode).stream()
                .filter(inv -> inv.getPdfPath() == null)
                .collect(Collectors.toList());
    }

    private void processSingleInvoice(Long invoiceId) {
        try {
            log.info("Invoice Processing Started for Invoice ID: {}", invoiceId);
            invoiceProcessingService.processInvoice(invoiceId);
            log.info("Invoice Processing Successful for Invoice ID: {}", invoiceId);
        } catch (Exception e) {
            log.error("Invoice Processing Failed for Invoice ID: {}. Reason: {}", invoiceId, e.getMessage(), e);
        }
    }
}



