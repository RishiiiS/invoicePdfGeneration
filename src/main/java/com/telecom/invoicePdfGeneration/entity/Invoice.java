package com.telecom.invoicePdfGeneration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "invoice")
public class Invoice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Long invoiceId;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "plan_id")
    private Long planId;

    @Column(name = "plan_charges")
    private BigDecimal planCharges;

    @Column(name = "usage_charges")
    private BigDecimal usageCharges;

    @Column(name = "other_charges")
    private BigDecimal otherCharges;

    @Column(name = "adjustments")
    private BigDecimal adjustments;

    @Column(name = "payments")
    private BigDecimal payments;

    @Column(name = "tax")
    private BigDecimal tax;

    @Column(name = "bill_due_amt")
    private BigDecimal billDueAmt;

    @Column(name = "pdf_path")
    private String pdfPath;

    @Column(name = "billing_cycle_id")
    private String billingCycleId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
