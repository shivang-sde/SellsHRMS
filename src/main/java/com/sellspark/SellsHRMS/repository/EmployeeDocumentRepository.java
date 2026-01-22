package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.EmployeeDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, Long> {

    List<EmployeeDocument> findByEmployeeId(Long employeeId);

    Optional<EmployeeDocument> findByEmployeeIdAndDocumentType(Long empId, String type);
}
