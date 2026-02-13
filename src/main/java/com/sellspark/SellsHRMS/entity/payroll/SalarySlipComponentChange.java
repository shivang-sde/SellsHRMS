package com.sellspark.SellsHRMS.entity.payroll;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

import com.sellspark.SellsHRMS.entity.User;

@Data
@Entity
@Table(name = "tbl_slip_component_change")
public class SalarySlipComponentChange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_slip_id")
    private SalarySlip salarySlip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private User changedBy;

    private LocalDateTime changedAt;

    @Column(name = "field_name")
    private String fieldName; // e.g., "BASIC", "HRA", "paymentDays"

    @Column(columnDefinition = "TEXT")
    private String beforeJson;

    @Column(columnDefinition = "TEXT")
    private String afterJson;

    @Column(length = 200)
    private String reason;
}
