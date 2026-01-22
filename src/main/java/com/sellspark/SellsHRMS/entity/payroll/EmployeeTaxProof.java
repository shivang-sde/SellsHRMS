package com.sellspark.SellsHRMS.entity.payroll;



import com.sellspark.SellsHRMS.entity.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_employee_tax_proof")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeTaxProof {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Employee employee;

    private String fiscalYear;
    private Double approvedAmount;
    private String proofType; // e.g. “LIC”, “HomeLoan”, “PPF”

    private String documentUrl; // optional attachment

    @Builder.Default
    private Boolean verified = false;
}
