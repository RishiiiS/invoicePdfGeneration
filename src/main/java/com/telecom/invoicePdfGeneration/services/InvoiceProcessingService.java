package com.telecom.invoicePdfGeneration.services;

import com.telecom.invoicePdfGeneration.dto.response.InvoicePdfResponseDto;

public interface InvoiceProcessingService {
    
    InvoicePdfResponseDto processInvoice(Long invoiceId);
}
