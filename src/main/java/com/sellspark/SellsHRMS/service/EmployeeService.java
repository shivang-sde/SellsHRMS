package com.sellspark.SellsHRMS.service;

import java.util.List;
import java.util.Optional;

import com.sellspark.SellsHRMS.dto.EmployeeDTO;
import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.Organisation;

public interface EmployeeService {

    Employee create(EmployeeDTO dto);

    List<Employee> getAll();

    List<Employee> getByOrganisationId(Long orgId);

    Optional<Employee> getById(Long id);

    List<Employee> getByOrganisation(Organisation organisation);

    Employee update(Long id, EmployeeDTO dto);

    void delete(Long id);
}
