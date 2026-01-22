package com.sellspark.SellsHRMS.service.payroll;

import com.sellspark.SellsHRMS.dto.payroll.PayRunDTO;
import com.sellspark.SellsHRMS.dto.payroll.PayRunDetailDTO;
import com.sellspark.SellsHRMS.dto.payroll.PayRunRequestDTO;

import java.time.LocalDate;
import java.util.List;


public interface PayRunService {

    PayRunDTO createPayRun(PayRunRequestDTO dto);

    // void deletePayRun();

    // PayRunDTO approvePayRun(Long payRunId);

    PayRunDTO processPayRun(Long payRunId);

    // PayRunDTO completePayRun(Long payRunId);

    List<PayRunDTO> getPayRuns(Long organisationId);

    PayRunDTO getPayRun(Long payRunId);

    PayRunDetailDTO getPayRunDetails(Long payRunId);

    // void cancelPayRun(Long payRunId);
}
