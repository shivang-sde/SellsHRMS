package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.EmployeeBank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeBankRepository extends JpaRepository<EmployeeBank, Long> {

    List<EmployeeBank> findByEmployeeId(Long employeeId);

    boolean existsByEmployeeIdAndIsPrimaryAccountTrue(Long employeeId);

    
    EmployeeBank findByEmployeeIdAndIsPrimaryAccountTrue(Long employeeId);
}
