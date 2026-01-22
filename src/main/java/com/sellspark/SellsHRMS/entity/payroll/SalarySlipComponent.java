package com.sellspark.SellsHRMS.entity.payroll;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import jakarta.persistence.*;
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "tbl_salary_slip_component")
public class SalarySlipComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private SalarySlip salarySlip;

    @ManyToOne(fetch = FetchType.LAZY)
    private SalaryComponent component;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statutory_component_id")
    private StatutoryComponent statutoryComponent; // e.g. PF, ESI, PT

     @Builder.Default
    private Boolean isStatutory = false;


    @Column(length = 20)
    private String componentType; // EARNING or DEDUCTION


    @Column(length = 100)
    private String componentName;
    private Double amount;

    
    @Column(length = 20)
    private String componentAbbreviation;


    private String sourceEngine; // FORMULA_ENGINE, STATUTORY_ENGINE, TAX_ENGINE, MANUAL
    private LocalDateTime createdAt = LocalDateTime.now();



    @Column(columnDefinition = "TEXT")
    private String calculationLog; // explanation of computation
    
    /*
    {
        "formula": "(BASIC + DA) * 0.12",
        "inputs": {"BASIC": 50000, "DA": 5000},
        "result": 6600    
    }

    */
}

