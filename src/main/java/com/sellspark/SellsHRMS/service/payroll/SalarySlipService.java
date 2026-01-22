package com.sellspark.SellsHRMS.service.payroll;

import com.sellspark.SellsHRMS.dto.payroll.SalarySlipDTO;
import java.util.List;

import org.springframework.stereotype.Service;


@Service
public interface SalarySlipService {

    SalarySlipDTO generateSlip(Long employeeId, Long payRunId);

    SalarySlipDTO updateSlip(Long slipId, SalarySlipDTO dto);

    void deactivateSlip(Long slipId);

    SalarySlipDTO getSlip(Long slipId);

    List<SalarySlipDTO> getSlipsByEmployee(Long employeeId);

    List<SalarySlipDTO> getSlipsByPayRun(Long payRunId);
}
