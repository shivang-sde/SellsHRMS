package com.sellspark.SellsHRMS.entity.payroll;

import com.sellspark.SellsHRMS.entity.Employee;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_payroll_approval")
public class PayrollApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Enumerated(EnumType.STRING)
    private ApprovalType type; // SALARY_REVISION, REIMBURSEMENT

    @Enumerated(EnumType.STRING)
    private ApprovalStatus status; // PENDING, APPROVED, REJECTED

    private String remarks;


    public enum ApprovalType {
    SALARY_REVISION("Salary Revision"),
    REIMBURSEMENT("Reimbursment"), 
    PROOF_OF_INVESTEMENT("Proof Of Investment");


    private final String displayName;
    
    ApprovalType(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    }

public enum ApprovalStatus {
    PENDING,
    APPROVED,
    REJECTED
}


}



