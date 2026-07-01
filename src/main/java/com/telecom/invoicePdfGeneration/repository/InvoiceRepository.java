package com.telecom.invoicePdfGeneration.repository;

import com.telecom.invoicePdfGeneration.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByPdfPathIsNull();

    List<Invoice> findByCustomerId(Long customerId);

    List<Invoice> findByBillingCycleId(String billingCycleId);
}
