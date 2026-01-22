package com.sellspark.SellsHRMS.entity.payroll;

import java.util.ArrayList;
import java.util.List;


import com.sellspark.SellsHRMS.entity.Organisation;
import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name = "tbl_salary_structure")
public class SalaryStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private PayrollFrequency payrollFrequency; // MONTHLY, BIWEEKLY, etc.

    @Enumerated(EnumType.STRING)
    private StructureType type; // GENERAL, TIMESHEET, FLEXIBLE

    private Double leaveEncashmentRate;
    private Double maxBenefits;
    private String currency;

    @ManyToMany
    @JoinTable(
        name = "tbl_salary_structure_component_map",
        joinColumns = @JoinColumn(name = "structure_id"),
        inverseJoinColumns = @JoinColumn(name = "component_id")
    )
    private List<SalaryComponent> components = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    @Column(length = 5)
    private String countryCode;

    private Boolean active = true;

    
public enum StructureType { GENERAL, TIMESHEET, FLEXIBLE }
public enum PayrollFrequency { MONTHLY, BIWEEKLY, WEEKLY }

}
