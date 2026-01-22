package com.sellspark.SellsHRMS.dto.payroll;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

import com.sellspark.SellsHRMS.entity.payroll.PayRun.PayRunStatus;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayRunDTO {

    private Long id;

    private Long organisationId;
    private LocalDate startDate;
    private LocalDate endDate;

    private PayRunStatus status; // READY, APPROVED, COMPLETED, CANCELLED

    private Double totalGross;
    private Double totalDeduction;
    private Double totalNet;

    private List<Long> salarySlipIds; // list of generated slips

    // public enum PayRunStatus { READY, APPROVED, COMPLETED, CANCELLED }
}
