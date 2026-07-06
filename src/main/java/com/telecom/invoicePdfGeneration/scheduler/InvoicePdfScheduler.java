package com.telecom.invoicePdfGeneration.scheduler;

import com.telecom.invoicePdfGeneration.services.BillingCycleSchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvoicePdfScheduler {

    private final BillingCycleSchedulerService billingCycleSchedulerService;

    @Scheduled(cron = "${scheduler.invoice.pdf.cron}")
    public void scheduleInvoicePdfGeneration() {
        log.info("Scheduler Started");
        billingCycleSchedulerService.processTodaysBillingCycle();
        log.info("Scheduler Completed");
    }
}
