package com.telecom.invoicePdfGeneration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "billing_cycle")
public class BillingCycle implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "cycle_code")
    private String cycleCode;

    @Column(name = "cycle_start_date")
    private LocalDate cycleStartDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "billing_date")
    private LocalDate billingDate;

    @Column(name = "remark")
    private String remark;

    @Column(name = "num_groups")
    private Integer numGroups;
}
