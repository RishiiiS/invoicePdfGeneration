package com.telecom.invoicePdfGeneration.services;

import org.springframework.core.io.Resource;

public interface PdfStorageService {
    
    String storePdf(byte[] pdfContent, String msisdn);

    Resource loadPdfAsResource(String fileName);
}
