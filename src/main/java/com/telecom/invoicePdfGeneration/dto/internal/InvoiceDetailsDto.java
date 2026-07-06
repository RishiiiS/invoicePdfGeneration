package com.telecom.invoicePdfGeneration.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDetailsDto {
    
    // Invoice Data
    private Long invoiceId;
    private BigDecimal planCharges;
    private BigDecimal usageCharges;
    private BigDecimal otherCharges;
    private BigDecimal adjustments;
    private BigDecimal payments;
    private BigDecimal tax;
    private BigDecimal billDueAmt;
    private String billingCycleId;
    
    // Customer Data
    private Long customerId;
    private String msisdn;
    private String firstName;
    private String lastName;
    private String email;
    
    // Plan Data
    private Integer planId;
    private String planName;
    private BigDecimal monthlyRental;
    
    // Tax Data
    private String taxName;
    private String taxValue;
    
    // Discount Data
    private String discountName;
    private String discountValue;
}
