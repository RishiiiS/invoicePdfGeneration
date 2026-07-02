package com.telecom.invoicePdfGeneration.services.impl;

import com.telecom.invoicePdfGeneration.dto.internal.InvoiceDetailsDto;
import com.telecom.invoicePdfGeneration.entity.Customer;
import com.telecom.invoicePdfGeneration.entity.Discount;
import com.telecom.invoicePdfGeneration.entity.Invoice;
import com.telecom.invoicePdfGeneration.entity.Plan;
import com.telecom.invoicePdfGeneration.entity.Tax;
import com.telecom.invoicePdfGeneration.repository.CustomerRepository;
import com.telecom.invoicePdfGeneration.repository.DiscountRepository;
import com.telecom.invoicePdfGeneration.repository.InvoiceRepository;
import com.telecom.invoicePdfGeneration.repository.PlanRepository;
import com.telecom.invoicePdfGeneration.repository.TaxRepository;
import com.telecom.invoicePdfGeneration.services.InvoiceAggregationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceAggregationServiceImpl implements InvoiceAggregationService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final PlanRepository planRepository;
    private final TaxRepository taxRepository;
    private final DiscountRepository discountRepository;

    @Override
    public InvoiceDetailsDto getInvoiceDetails(Long invoiceId) {
        Invoice invoice = getInvoice(invoiceId);
        Customer customer = getCustomer(invoice.getCustomerId());
        Plan plan = getPlan(invoice.getPlanId());
        Tax activeTax = getActiveTax();
        Discount activeDiscount = getActiveDiscount();

        return buildInvoiceDetailsDto(invoice, customer, plan, activeTax, activeDiscount);
    }

    private Invoice getInvoice(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));
    }

    private Customer getCustomer(Long customerId) {
        if (customerId == null) {
            throw new RuntimeException("Customer ID is null in the invoice.");
        }
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
    }

    private Plan getPlan(Long planId) {
        if (planId == null) {
            throw new RuntimeException("Plan ID is null in the invoice.");
        }
        return planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found with ID: " + planId));
    }

    private Tax getActiveTax() {
        List<Tax> taxes = taxRepository.findAll();
        return taxes.stream()
                .filter(t -> "ACTIVE".equalsIgnoreCase(t.getStatus()))
                .findFirst()
                .orElseGet(() -> taxes.isEmpty() ? new Tax() : taxes.get(0));
    }

    private Discount getActiveDiscount() {
        List<Discount> discounts = discountRepository.findAll();
        return discounts.stream()
                .filter(d -> "ACTIVE".equalsIgnoreCase(d.getStatus()))
                .findFirst()
                .orElseGet(() -> discounts.isEmpty() ? new Discount() : discounts.get(0));
    }

    private InvoiceDetailsDto buildInvoiceDetailsDto(Invoice invoice, Customer customer, Plan plan, Tax tax, Discount discount) {
        return InvoiceDetailsDto.builder()
                .invoiceId(invoice.getInvoiceId())
                .planCharges(invoice.getPlanCharges())
                .usageCharges(invoice.getUsageCharges())
                .otherCharges(invoice.getOtherCharges())
                .adjustments(invoice.getAdjustments())
                .payments(invoice.getPayments())
                .tax(invoice.getTax())
                .billDueAmt(invoice.getBillDueAmt())
                .billingCycleId(invoice.getBillingCycleId())
                
                .customerId(customer.getCustomerId())
                .msisdn(customer.getMsisdn())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                
                .planId(plan.getPlanId())
                .planName(plan.getPlanName())
                .monthlyRental(plan.getMonthlyRental())
                
                .taxName(tax.getTaxName())
                .taxPercentage(tax.getTaxPercentage())
                
                .discountName(discount.getDiscountName())
                .discountAmount(discount.getDiscountAmount())
                .discountPercentage(discount.getDiscountPercentage())
                .build();
    }
}
