package com.telecom.invoicePdfGeneration.controllers;

import com.telecom.invoicePdfGeneration.dto.response.ApiResponseDto;
import com.telecom.invoicePdfGeneration.dto.response.InvoicePdfResponseDto;
import com.telecom.invoicePdfGeneration.services.BillingCycleSchedulerService;
import com.telecom.invoicePdfGeneration.services.InvoiceProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Slf4j
public class InvoicePdfController {

    private final InvoiceProcessingService invoiceProcessingService;
    private final BillingCycleSchedulerService billingCycleSchedulerService;

    @PostMapping("/{invoiceId}/generate-pdf")
    public ResponseEntity<InvoicePdfResponseDto> generatePdfForInvoice(@PathVariable Long invoiceId) {
        log.info("Manual invoice generation requested for Invoice ID: {}", invoiceId);
        InvoicePdfResponseDto response = invoiceProcessingService.processInvoice(invoiceId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate/today")
    public ResponseEntity<ApiResponseDto> generatePdfForTodaysCycle() {
        log.info("Today's billing manually triggered.");
        billingCycleSchedulerService.processTodaysBillingCycle();
        
        ApiResponseDto response = ApiResponseDto.builder()
                .success(true)
                .message("Today's billing cycle executed successfully.")
                .build();
                
        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate/cycle/{cycleCode}")
    public ResponseEntity<ApiResponseDto> generatePdfForSpecificCycle(@PathVariable String cycleCode) {
        log.info("Billing cycle manually triggered for cycle: {}", cycleCode);
        billingCycleSchedulerService.processBillingCycle(cycleCode);
        
        ApiResponseDto response = ApiResponseDto.builder()
                .success(true)
                .message("Billing cycle " + cycleCode + " processed successfully.")
                .build();
                
        return ResponseEntity.ok(response);
    }
}
