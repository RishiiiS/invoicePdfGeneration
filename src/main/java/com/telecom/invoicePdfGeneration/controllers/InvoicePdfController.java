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

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import com.telecom.invoicePdfGeneration.services.PdfStorageService;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Slf4j
public class InvoicePdfController {

    private final InvoiceProcessingService invoiceProcessingService;
    private final BillingCycleSchedulerService billingCycleSchedulerService;
    private final PdfStorageService pdfStorageService;

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

    // download pdf by month

    @GetMapping("/download/{month}/{fileName:.+}")
    public ResponseEntity<Resource> downloadInvoicePdf(@PathVariable String month, @PathVariable String fileName) {
        log.info("Download requested for file: {}/{}", month, fileName);
        try {
            // Pass the concatenated path to the service
            Resource resource = pdfStorageService.loadPdfAsResource(month + "/" + fileName);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("Error downloading file {}/{}: {}", month, fileName, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}