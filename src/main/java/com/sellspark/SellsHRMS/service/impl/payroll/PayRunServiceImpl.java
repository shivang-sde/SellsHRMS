package com.sellspark.SellsHRMS.service.impl.payroll;


import com.sellspark.SellsHRMS.dto.payroll.PayRunDTO;
import com.sellspark.SellsHRMS.dto.payroll.PayRunDetailDTO;
import com.sellspark.SellsHRMS.dto.payroll.PayRunRequestDTO;
import com.sellspark.SellsHRMS.dto.payroll.SalarySlipComponentDTO;
import com.sellspark.SellsHRMS.dto.payroll.SalarySlipDTO;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.payroll.*;
import com.sellspark.SellsHRMS.exception.DuplicateResourceException;
import com.sellspark.SellsHRMS.exception.OrganisationNotFoundException;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.repository.payroll.PayRunRepository;
import com.sellspark.SellsHRMS.service.payroll.PayRunService;
import com.sellspark.SellsHRMS.service.payroll.PayrollCalculationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PayRunServiceImpl implements PayRunService {

    private final OrganisationRepository orgRepo;
    private final PayRunRepository payRunRepo;
    private final PayrollCalculationService engineService;

    public PayRunDTO createPayRun(PayRunRequestDTO dto) {
    Organisation org = orgRepo.findById(dto.getOrganisationId())
        .orElseThrow(() -> new OrganisationNotFoundException(dto.getOrganisationId()));

    // Parse dates
    LocalDate start = LocalDate.parse(dto.getStartDate());
    LocalDate end = LocalDate.parse(dto.getEndDate());

    // Derive month/year from payPeriod ("YYYY-MM")
    YearMonth ym = YearMonth.parse(dto.getPayPeriod());
    int month = ym.getMonthValue();
    int year = ym.getYear();

    // Check duplicate
    PayRun prun =  payRunRepo.findByOrganisation_IdAndMonthAndYear(org.getId(), month, year);
     log.info("prun {}", prun);
     if(prun != null) {
        throw new  DuplicateResourceException("A PayRun already exists for this period");
     }

    PayRun run = new PayRun();
    run.setOrganisation(org);
    run.setStartDate(start);
    run.setEndDate(end);
    run.setMonth(month);
    run.setYear(year);
    run.setPeriodLabel(ym.getMonth().name() + " " + year); // e.g. "JANUARY 2026"
    run.setStatus(PayRun.PayRunStatus.READY);

    return mapToDto(payRunRepo.save(run));
}


    @Override
@Transactional
public PayRunDTO processPayRun(Long payRunId) {

    PayRun payRun = payRunRepo.findById(payRunId)
            .orElseThrow(() -> new ResourceNotFoundException("PayRun not found"));

    Long orgId = payRun.getOrganisation().getId();

    // Trigger full payroll computation
    List<SalarySlipDTO> slips = engineService.runPayroll(orgId, payRun);

    // Compute PayRun-level totals
    double totalGross = slips.stream().mapToDouble(SalarySlipDTO::getGrossPay).sum();
    double totalDeduction = slips.stream().mapToDouble(SalarySlipDTO::getTotalDeductions).sum();
    double totalNet = slips.stream().mapToDouble(SalarySlipDTO::getNetPay).sum();

    payRun.setTotalGross(totalGross);
    payRun.setTotalDeduction(totalDeduction);
    payRun.setTotalNet(totalNet);
    payRun.setStatus(PayRun.PayRunStatus.COMPLETED);
    payRun.setRunDate(LocalDate.now());
    payRun.setPeriodLabel(
            payRun.getStartDate().getMonth().name() + " " + payRun.getStartDate().getYear()
    );

    payRunRepo.save(payRun);

    //  Map updated payRun to DTO for front-end summary
    return PayRunDTO.builder()
            .id(payRun.getId())
            .organisationId(orgId)
            .startDate(payRun.getStartDate())
            .endDate(payRun.getEndDate())
            .status(payRun.getStatus())
            .totalGross(totalGross)
            .totalDeduction(totalDeduction)
            .totalNet(totalNet)
            .build();
}


    public List<PayRunDTO> getPayRuns(Long orgId) {
        return payRunRepo.findByOrganisation_IdOrderByStartDateDesc(orgId).stream().map(this::mapToDto).collect(Collectors.toList());
    }


    public PayRunDTO getPayRun(Long payRunId) {
        PayRun pr = payRunRepo.findById(payRunId).orElseThrow(() -> new ResourceNotFoundException("Pay run not found"));
        return mapToDto(pr);
    }


    @Override
public PayRunDetailDTO getPayRunDetails(Long payRunId) {
    PayRun payRun = payRunRepo.findById(payRunId)
            .orElseThrow(() -> new ResourceNotFoundException("PayRun not found"));

    double totalGross = 0.0;
    double totalDeduction = 0.0;
    double totalNet = 0.0;

    List<SalarySlipDTO> slips = payRun.getSalarySlips().stream().map(slip ->
            SalarySlipDTO.builder()
                    .id(slip.getId())
                    .employeeId(slip.getEmployee().getId())
                    .employeeName(slip.getEmployee().getFirstName() + " " + slip.getEmployee().getLastName())
                    .grossPay(slip.getGrossPay())
                    .totalDeductions(slip.getTotalDeductions())
                    .netPay(slip.getNetPay())
                    .fromDate(slip.getFromDate())
                    .toDate(slip.getToDate())
                    .payRunId(payRun.getId())
                    .components(
                            slip.getComponents().stream().map(c ->
                                    SalarySlipComponentDTO.builder()
                                            .componentName(c.getComponentName())
                                            .componentType(c.getComponentType())
                                            .amount(c.getAmount())
                                            .build()
                            ).toList()
                    )
                    .build()
    ).toList();

    for (SalarySlipDTO slip : slips) {
        totalGross += slip.getGrossPay() != null ? slip.getGrossPay() : 0;
        totalDeduction += slip.getTotalDeductions() != null ? slip.getTotalDeductions() : 0;
        totalNet += slip.getNetPay() != null ? slip.getNetPay() : 0;
    }

    return PayRunDetailDTO.builder()
            .id(payRun.getId())
            .periodLabel(payRun.getPeriodLabel())
            .startDate(payRun.getStartDate())
            .endDate(payRun.getEndDate())
            .status(payRun.getStatus().name())
            .totalGross(totalGross)
            .totalDeduction(totalDeduction)
            .totalNet(totalNet)
            .slips(slips)
            .build();
}




    public PayRunDTO mapToDto(PayRun pr){
        return PayRunDTO.builder()
                .id(pr.getId())
                .organisationId(pr.getOrganisation().getId())
                .startDate(pr.getStartDate())
                .endDate(pr.getEndDate())
                .status(pr.getStatus())
                .build();
    }
}
