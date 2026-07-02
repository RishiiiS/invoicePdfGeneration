package com.telecom.invoicePdfGeneration.services;

public interface PdfStorageService {
    
    String storePdf(byte[] pdfContent, String msisdn);
}
