package com.sellspark.SellsHRMS.entity.payroll;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.Organisation;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_salary_slip")
public class SalarySlip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    private EmployeeSalaryAssignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    private PayRun payRun;

    private LocalDate fromDate;
    private LocalDate toDate;

    private Double workingDays;
    private Double paymentDays;
    private Double lopDays;

    private Double grossPay;
    private Double totalDeductions;
    private Double netPay;

    @Column(name = "statutory_contribution_org")
    private Double statutoryContributionOrg;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private SlipStatus status = SlipStatus.DRAFT;

    @Builder.Default
    private LocalDate generatedAt = LocalDate.now();
    @Column(name = "credited_at")
    private LocalDateTime creditedAt;

    @Builder.Default
    private Boolean isCredited = false;

    @Column(name = "pdf_url")
    private String pdfUrl;

    @Column(name = "pdf_path")
    private String pdfPath;

    @Lob
    @Column(name = "pdf_data")
    private byte[] pdfData;// storing directly in DB, if needed

    // optional snapshot of structure for historical reference
    @Column(columnDefinition = "JSON")
    private String structureSnapshot;

    @Builder.Default
    @OneToMany(mappedBy = "salarySlip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalarySlipComponent> components = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private SalarySlipTemplate template;

    @Column(name = "template_version")
    private Integer templateVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    @Builder.Default
    @Version
    @Column(nullable = false)
    private Long version = 0L;

    public enum SlipStatus {
        DRAFT, // Generated but not approved
        GENERATED,
        APPROVED, // HR reviewed
        FINALIZED, // Locked, part of completed payroll
        CANCELLED // Reversed or voided
    }

}
