package com.telecom.invoicePdfGeneration.services;

import com.telecom.invoicePdfGeneration.dto.internal.InvoiceDetailsDto;

public interface PdfGenerationService {
    
    byte[] generateInvoicePdf(InvoiceDetailsDto invoiceDetails);
}
