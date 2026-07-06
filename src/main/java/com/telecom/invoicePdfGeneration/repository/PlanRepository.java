package com.telecom.invoicePdfGeneration.repository;

import com.telecom.invoicePdfGeneration.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {
}
