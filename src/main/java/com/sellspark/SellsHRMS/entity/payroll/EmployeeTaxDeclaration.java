package com.sellspark.SellsHRMS.entity.payroll;

import java.time.LocalDate;

import com.sellspark.SellsHRMS.entity.Employee;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "tbl_employee_tax_declaration")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeTaxDeclaration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Employee employee;

    private String fiscalYear; // e.g. 2025-2026
    private Double totalDeclaredAmount; // sum of all sections (provisional)
    private Boolean proofSubmitted = false;
    private Boolean verified = false;

    @Column(columnDefinition = "TEXT")
    private String detailsJson; // JSON map of { sectionCode: amount }

    private LocalDate submittedAt;
}
