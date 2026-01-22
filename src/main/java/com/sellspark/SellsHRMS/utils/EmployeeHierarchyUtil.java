package com.sellspark.SellsHRMS.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;


import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.exception.EmployeeNotFoundException;
import com.sellspark.SellsHRMS.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeHierarchyUtil {

    private final EmployeeRepository employeeRepository;

    public Set<Long> getAllSubordinateIds(Long managerId) {
        Set<Long> result = new HashSet<>();
        collectSubordinates(managerId, result);
        return result;
    }

    private void collectSubordinates(Long managerId, Set<Long> result) {
        Employee manager = employeeRepository.findById(managerId)
        .orElseThrow(() -> new EmployeeNotFoundException(managerId));
        List<Employee> subs = employeeRepository.findByReportingTo(manager);
        for (Employee sub : subs) {
            if (result.add(sub.getId())) {
                collectSubordinates(sub.getId(), result);
            }
        }
    }
}
