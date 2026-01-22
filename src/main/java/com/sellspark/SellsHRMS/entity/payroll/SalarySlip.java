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


    private LocalDate generatedAt = LocalDate.now();


    private Double workingDays;
    private Double paymentDays;
    private Double lopDays;

    private Double grossPay;
    private Double totalDeductions;
    private Double netPay;


    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SlipStatus status = SlipStatus.DRAFT;


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

    @OneToMany(mappedBy = "salarySlip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalarySlipComponent> components = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;


    public enum SlipStatus {
        DRAFT,      // Generated but not approved
        GENERATED,
        APPROVED,   // HR reviewed
        FINALIZED,  // Locked, part of completed payroll
        CANCELLED   // Reversed or voided
    }

}

