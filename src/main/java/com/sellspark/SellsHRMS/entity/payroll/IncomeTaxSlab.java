package com.sellspark.SellsHRMS.entity.payroll;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.sellspark.SellsHRMS.entity.Organisation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "tbl_income_tax_slab")
public class IncomeTaxSlab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String countryCode;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    @Builder.Default
    private Boolean allowTaxExemption = false;

    @Builder.Default
    private Double standardExemptionLimit = 0.0;

    @Builder.Default
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    @Builder.Default
    @OneToMany(mappedBy = "taxSlab", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IncomeTaxRule> rules = new ArrayList<>();
}
