package com.telecom.invoicePdfGeneration.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoicePdfResponseDto {
    
    private Long invoiceId;
    private Long customerId;
    private String msisdn;
    private String pdfPath;
    private String status;
    private String message;
}
