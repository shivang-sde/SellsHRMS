package com.sellspark.SellsHRMS.entity.payroll;

import java.time.LocalDate;

import com.sellspark.SellsHRMS.entity.Organisation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_statutory_rule")
public class StatutoryRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Each rule belongs to one statutory component
    @ManyToOne
    @JoinColumn(name = "statutory_component_id", nullable = false)
    private StatutoryComponent statutoryComponent;

    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "employer_contribution_percent")
    private Double employerContributionPercent;

    @Column(name = "employee_contribution_percent")
    private Double employeeContributionPercent;

    @Column(name = "min_applicable_salary")
    private Double minApplicableSalary;

    @Column(name = "max_applicable_salary")
    private Double maxApplicableSalary;

    @Builder.Default
    @Column(name = "apply_pro_rata")
    private Boolean applyProRata = false;

    @Builder.Default
    @Column(name = "include_employee_cnotribution_in_ctc")
    private Boolean includeEmployeeContriPercentInCTC = true;

    @Builder.Default
    @Column(name = "include__employer_cnotribution_in_ctc")
    private Boolean includeEmployerContriPercentInCTC = true;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private DeductionCycle deductionCycle = DeductionCycle.MONTHLY;

    @Column(name = "additional_config", columnDefinition = "TEXT")
    private String additionalConfig; // JSON for advanced country-specific rules


    @Builder.Default
    private Boolean active = true;

    public enum DeductionCycle {
    MONTHLY,
    QUARTERLY,
    HALF_YEARLY,
    YEARLY,
    CUSTOM // e.g., once per financial year, on-demand
}

}

