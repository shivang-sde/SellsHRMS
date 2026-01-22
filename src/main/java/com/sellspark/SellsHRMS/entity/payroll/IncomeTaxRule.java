package com.sellspark.SellsHRMS.entity.payroll;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_income_tax_rule")
public class IncomeTaxRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_salb_id")
    private IncomeTaxSlab taxSlab;

    private Double minIncome;
    private Double maxIncome;
    private Double deductionPercent;


    @Column(name = "`condition`", columnDefinition = "TEXT")
    private String condition; // optional JSON/EL expression for special cases
}
