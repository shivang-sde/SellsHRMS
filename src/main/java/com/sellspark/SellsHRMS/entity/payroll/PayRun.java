package com.sellspark.SellsHRMS.entity.payroll;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.sellspark.SellsHRMS.entity.Organisation;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_pay_run",
     uniqueConstraints = { @UniqueConstraint(columnNames = {"month", "year", "organisation_id"})}
)
public class PayRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    private LocalDate startDate;
    private LocalDate endDate;

    private String periodLabel; // e.g. "January 2026"
    

    private LocalDate runDate;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private PayRunStatus status = PayRunStatus.READY;

    @Builder.Default
    private Double totalGross = 0.0;

    @Builder.Default
    private Double totalDeduction = 0.0;

    @Builder.Default
    private Double totalNet = 0.0;

    @Builder.Default
    @OneToMany(mappedBy = "payRun", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalarySlip> salarySlips = new ArrayList<>();

    @Column(name="month")
    private Integer month; // 1-12

    @Column(name="year")
    private Integer year;

    public enum PayRunStatus { PROCESSING, READY, APPROVED, COMPLETED, CANCELLED }
}


