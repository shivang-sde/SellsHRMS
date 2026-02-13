package com.sellspark.SellsHRMS.service.payroll;

import java.util.List;

import com.sellspark.SellsHRMS.dto.common.PagedResponse;
import com.sellspark.SellsHRMS.dto.payroll.SalarySlipDTO;

public interface AccountantService {

    PagedResponse<SalarySlipDTO> getSalarySlips(
            Long orgId,
            Integer month,
            Integer year,
            Boolean credited,
            Long departmentId,
            String search,
            int page,
            int size,
            String sort);

    SalarySlipDTO markSalaryCredited(Long orgId, Long slipId, Long accountantUserId);

    PagedResponse<SalarySlipDTO> markBulkSalaryCredited(Long orgId, List<Long> slipIds, Long accountantUserId);

    List<String> generateBulkSlipPdfs(Long orgId, List<Long> slipIds);

    String generateSlipPdf(Long orgId, Long slipId);

}
