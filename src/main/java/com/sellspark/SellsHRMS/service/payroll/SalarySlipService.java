package com.sellspark.SellsHRMS.service.payroll;

import com.sellspark.SellsHRMS.dto.payroll.SalarySlipDTO;
import com.sellspark.SellsHRMS.entity.payroll.SalarySlip;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface SalarySlipService {

    SalarySlip generatePdfForSlip(Long slipId, Long orgId);

    SalarySlip saveSlip(SalarySlip slip);

    List<SalarySlip> getEmployeeSlips(Long empId);

    SalarySlip getSlip(Long slipId);

    List<SalarySlipDTO> getAllByEmployee(Long empId);

    // List<SalarySlipDTO> getSlipsByPayRun(Long payRunId);

    SalarySlipDTO getSalarySlipDtoById(Long id);

    // SalarySlipDTO generateSlip(Long employeeId, Long payRunId);

    // SalarySlipDTO updateSlip(Long slipId, SalarySlipDTO dto);

}
