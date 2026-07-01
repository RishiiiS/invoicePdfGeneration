package com.telecom.invoicePdfGeneration.repository;

import com.telecom.invoicePdfGeneration.entity.Tax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxRepository extends JpaRepository<Tax, Long> {
}
