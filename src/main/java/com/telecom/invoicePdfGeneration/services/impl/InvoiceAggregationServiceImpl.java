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

import java.time.LocalDate;
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

    private Plan getPlan(Integer planId) {
        if (planId == null) {
            return new Plan();
        }
        return planRepository.findById(planId).orElse(new Plan());
    }

    private Tax getActiveTax() {
        List<Tax> taxes = taxRepository.findAll();
        LocalDate now = LocalDate.now();
        return taxes.stream()
                .filter(t -> t.getExpirationDt() == null || t.getExpirationDt().isAfter(now))
                .findFirst()
                .orElse(new Tax());
    }

    private Discount getActiveDiscount() {
        List<Discount> discounts = discountRepository.findAll();
        LocalDate now = LocalDate.now();
        return discounts.stream()
                .filter(d -> d.getExpirationDt() == null || d.getExpirationDt().isAfter(now))
                .findFirst()
                .orElse(new Discount());
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
                .email(customer.getEmailId())
                
                .planId(plan.getPlanId())
                .planName(plan.getPlanName() != null ? plan.getPlanName() : "N/A")
                .monthlyRental(plan.getMonthlyCharge())
                
                .taxName(tax.getTaxValue() != null ? tax.getTaxValue() : "N/A")
                .taxValue(tax.getPropertyValue())
                
                .discountName(discount.getDiscountValue() != null ? discount.getDiscountValue() : "N/A")
                .discountValue(discount.getPropertyValue())
                .build();
    }
}
