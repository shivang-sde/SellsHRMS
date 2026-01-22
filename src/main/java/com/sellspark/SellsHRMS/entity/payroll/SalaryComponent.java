package com.sellspark.SellsHRMS.entity.payroll;

import java.time.LocalDateTime;

import com.sellspark.SellsHRMS.entity.Organisation;

import jakarta.persistence.*;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "tbl_salary_component",
 uniqueConstraints = { @UniqueConstraint(columnNames = {"name", "organisation_id"})} )
public class SalaryComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., Basic Pay, HRA, EPF, Health Allowance

    private String description;

    @Column(nullable = false, unique = true, length = 20)
    private String abbreviation; // e.g., BASIC, HRA, PF

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ComponentType type; // EARNING, DEDUCTION 

    @Enumerated(EnumType.STRING)
    private CalculationType calculationType; // FIXED, FORMULA, PERCENTAGE

    private Double amount;

    // have to add fixed amount here if it is type fixed,
    // or if it is based on other slary component or base then
    //  the amount will be assigend to value calculated based formula.

    // but for other type it is not possible to assign calculate value component can be depend upon basepay, and basePay is in employee salary assignment 

    @Builder.Default
    private Boolean isTaxApplicable = false;
     
    @Builder.Default
    private Boolean isFlexibleBenefit = false;

    private Double maxFlexibleBenefitAmount; // if flexible
    

    // this filed will decide whether the amount of this comp will be calculated based on absent or leave.
    @Builder.Default
    private Boolean dependsOnPaymentDays = false;


    @Builder.Default
    private Boolean includeInCTC = true;
    
    @Builder.Default
    private Boolean roundToNearest = false;
    
    //  @Builder.Default
    // private Boolean isStatistical = false; // not shown on payslip

    @Column(columnDefinition = "TEXT")
    private String formula; // e.g., BASE * 0.4

    @Column(name = "component_condition", columnDefinition = "TEXT")
    private String componentCondition; // optional conditional logic

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;


     @Builder.Default
    private Boolean active = true;

    // @Enumerated(EnumType.STRING)
    // private ComponentGroup groupType; // EARNING_GROUP, DEDUCTION_GROUP

    // --- Auditing fields ---
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() { this.createdAt = LocalDateTime.now(); }

    @PreUpdate
    public void onUpdate() { this.updatedAt = LocalDateTime.now(); }

public enum ComponentType { EARNING, DEDUCTION
    // REIMBURSEMENT
 }
public enum CalculationType { FIXED, FORMULA, PERCENTAGE }
// public enum ComponentGroup { EARNING_GROUP, DEDUCTION_GROUP }

}
