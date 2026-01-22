package com.sellspark.SellsHRMS.entity.payroll;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.Organisation;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_employee_salary_assignment")
public class EmployeeSalaryAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_structure_id")
    private SalaryStructure salaryStructure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_slab_id")
    private IncomeTaxSlab taxSlab;

    private Double basePay;
    private Double variablePay;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private String remarks;

    private Boolean active = true;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL)
    private List<SalarySlip> salarySlips = new ArrayList<>();
}

