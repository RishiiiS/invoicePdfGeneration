package com.telecom.invoicePdfGeneration.services.impl;

import com.telecom.invoicePdfGeneration.dto.internal.InvoiceDetailsDto;
import com.telecom.invoicePdfGeneration.dto.response.InvoicePdfResponseDto;
import com.telecom.invoicePdfGeneration.entity.Invoice;
import com.telecom.invoicePdfGeneration.repository.InvoiceRepository;
import com.telecom.invoicePdfGeneration.services.InvoiceAggregationService;
import com.telecom.invoicePdfGeneration.services.InvoiceProcessingService;
import com.telecom.invoicePdfGeneration.services.PdfGenerationService;
import com.telecom.invoicePdfGeneration.services.PdfStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceProcessingServiceImpl implements InvoiceProcessingService {

    private final InvoiceAggregationService invoiceAggregationService;
    private final PdfGenerationService pdfGenerationService;
    private final PdfStorageService pdfStorageService;
    private final InvoiceRepository invoiceRepository;

    @Override
    @Transactional
    public InvoicePdfResponseDto processInvoice(Long invoiceId) {
        log.info("Processing Invoice = {}", invoiceId);
        
        // 1. Retrieve aggregated details
        InvoiceDetailsDto invoiceDetails = invoiceAggregationService.getInvoiceDetails(invoiceId);
        log.info("Aggregation Completed for Invoice = {}", invoiceId);

        // 2. Generate PDF document bytes
        byte[] pdfBytes = pdfGenerationService.generateInvoicePdf(invoiceDetails);
        log.info("PDF Generated for Invoice = {}. PDF Byte Size = {}", invoiceId, pdfBytes != null ? pdfBytes.length : 0);

        // 3. Store PDF onto the filesystem/storage
        String storedPdfPath = pdfStorageService.storePdf(pdfBytes, invoiceDetails.getMsisdn());
        log.info("PDF Stored = {}", storedPdfPath);

        // 4. Update and persist the invoice record
        updateInvoiceRecord(invoiceId, storedPdfPath);
        log.info("Database Updated for Invoice = {}", invoiceId);

        // 5. Construct and return response
        log.info("Invoice Completed = {}", invoiceId);
        return buildResponseDto(invoiceDetails, storedPdfPath);
    }

    private void updateInvoiceRecord(Long invoiceId, String pdfPath) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));
        
        invoice.setPdfPath(pdfPath);
        invoiceRepository.save(invoice);
    }

    private InvoicePdfResponseDto buildResponseDto(InvoiceDetailsDto details, String pdfPath) {
        return InvoicePdfResponseDto.builder()
                .invoiceId(details.getInvoiceId())
                .customerId(details.getCustomerId())
                .msisdn(details.getMsisdn())
                .pdfPath(pdfPath)
                .status("SUCCESS")
                .message("PDF successfully generated and stored.")
                .build();
    }
}
