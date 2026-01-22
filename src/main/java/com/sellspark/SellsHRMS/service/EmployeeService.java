package com.sellspark.SellsHRMS.service;
import com.sellspark.SellsHRMS.dto.employee.EmployeeCreateRequest;
import com.sellspark.SellsHRMS.dto.employee.EmployeeDetailResponse;
import com.sellspark.SellsHRMS.dto.employee.EmployeeResponse;

import java.time.LocalDate;
import java.util.List;


public interface EmployeeService {
    EmployeeResponse create(EmployeeCreateRequest request);
    EmployeeResponse update(Long id, EmployeeCreateRequest request);
    List<EmployeeResponse> getAll(Long organisationId);
    EmployeeDetailResponse getById(Long id);
    EmployeeDetailResponse getByIdAndOrg(Long id, Long orgId);


    List<EmployeeResponse> findUpcomingWorkAnniversaries( Long orgId, LocalDate startDate, LocalDate endDate);
     List<EmployeeResponse>findUpcomingBirthdays( Long orgId, LocalDate startDate, LocalDate endDate);

    // Employee getDetailEmpById(Long id);
    void softDelete(Long id);

    EmployeeResponse updateStatus(Long id, String status);

    List<EmployeeResponse> getSubordinates(Long managerId, Long organisationId);


}