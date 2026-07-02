package com.telecom.invoicePdfGeneration.services;

import com.telecom.invoicePdfGeneration.dto.internal.InvoiceDetailsDto;

public interface InvoiceAggregationService {
    
    InvoiceDetailsDto getInvoiceDetails(Long invoiceId);
}
