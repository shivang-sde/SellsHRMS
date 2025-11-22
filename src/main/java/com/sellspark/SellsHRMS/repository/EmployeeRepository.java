package com.sellspark.SellsHRMS.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.Organisation;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    List<Employee> findAllByOrganisationId(Long orgId);

    List<Employee> findByOrganisation(Organisation organisation);

    Optional<Employee> findByEmployeeCode(String code);

    int countByOrganisationId(Long organisationId);
}
