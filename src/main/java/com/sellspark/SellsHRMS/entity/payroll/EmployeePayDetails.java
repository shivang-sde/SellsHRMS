package com.sellspark.SellsHRMS.entity.payroll;


import com.sellspark.SellsHRMS.entity.Employee;

import jakarta.persistence.*;
import lombok.Builder;

@Builder
@Entity
@Table(name = "employee_pay_details")
public class EmployeePayDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pay_run_id")
    private PayRun payRun;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "component_id")
    private SalaryComponent salaryComponent;

    
    private Double amount;
    private Boolean taxable;

    private Integer lopDays; // Loss of pay days if applicable
}

