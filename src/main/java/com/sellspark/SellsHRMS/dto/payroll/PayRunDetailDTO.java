package com.sellspark.SellsHRMS.dto.payroll;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayRunDetailDTO {
    private Long id;
    private String periodLabel;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    private Double totalGross;
    private Double totalDeduction;
    private Double totalNet;

    private List<SalarySlipDTO> slips;
}
