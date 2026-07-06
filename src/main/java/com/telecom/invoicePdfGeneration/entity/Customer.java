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
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "customer")
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "msisdn")
    private String msisdn;

    @Column(name = "plan_code")
    private String planCode;

    @Column(name = "email_id")
    private String emailId;

    @Column(name = "alternate_number")
    private String alternateNumber;

    @Column(name = "cycle_code")
    private String cycleCode;

    @Column(name = "next_billing_date")
    private LocalDate nextBillingDate;

    @Column(name = "status")
    private String status;

    @Column(name = "stage_index")
    private Integer stageIndex;

    @Column(name = "failed")
    private Boolean failed;

    @Column(name = "fail_reason")
    private String failReason;

    @Column(name = "failed_at")
    private LocalDateTime failedAt;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "run_tag")
    private String runTag;

    @Column(name = "group_id")
    private Integer groupId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
