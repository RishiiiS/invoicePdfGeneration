package com.telecom.invoicePdfGeneration.repository;

import com.telecom.invoicePdfGeneration.entity.BillingCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface BillingCycleRepository extends JpaRepository<BillingCycle, String> {
    
    Optional<BillingCycle> findByBillingDate(LocalDate billingDate);
}
