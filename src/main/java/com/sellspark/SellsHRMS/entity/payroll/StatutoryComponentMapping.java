package com.sellspark.SellsHRMS.entity.payroll;

import com.sellspark.SellsHRMS.entity.Organisation;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(
    name = "tbl_statutory_component_mapping",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"statutory_component_id", "salary_component_id", "organisation_id"}
        )
    }
)
public class StatutoryComponentMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Link to the statutory component (e.g. EPF, ESI, SSS) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statutory_component_id", nullable = false)
    private StatutoryComponent statutoryComponent;

    /** Link to salary component (e.g. Basic Pay, Gross Pay, HRA) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_component_id", nullable = false)
    private SalaryComponent salaryComponent;

    /** Organisation context — makes mapping multi-tenant */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    /** Optional — override contribution % for this mapping only */
    private Double employeePercent;
    private Double employerPercent;

    /** Optional JSON — custom conditions or filters */
    @Column(columnDefinition = "TEXT")
    private String customRuleConfig;

    /** Country/state scoping for statutory variation */
    @Column(length = 5)  
    private String countryCode;

    @Column(length = 5)
    private String stateCode;

    /** Auditing and flags */
    @Builder.Default
    private Boolean active = true;

    @Builder.Default
    private Boolean includeInCalculation = true;
}
