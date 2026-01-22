package com.sellspark.SellsHRMS.entity;

import com.sellspark.SellsHRMS.entity.payroll.SalaryComponent;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_employee_salary_detail")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmployeeSalaryDetail {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="component_id")
    private SalaryComponent component;

    private Double amount;
    private java.time.LocalDate effectiveFrom;
    private java.time.LocalDate effectiveTo;
}