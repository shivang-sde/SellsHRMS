package com.sellspark.SellsHRMS.service;

import com.sellspark.SellsHRMS.dto.dashboard.MyWorkSummaryDTO;

public interface DashboardProjectService {
    MyWorkSummaryDTO getEmployeeDashboard(Long organisationId, Long employeeId);
}
