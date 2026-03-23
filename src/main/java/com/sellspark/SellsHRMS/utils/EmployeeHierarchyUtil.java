package com.sellspark.SellsHRMS.utils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.exception.employee.EmployeeNotFoundException;
import com.sellspark.SellsHRMS.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeHierarchyUtil {

    private final EmployeeRepository employeeRepository;

    public Set<Long> getAllSubordinateIds(Long managerId) {
        Set<Long> visited = new HashSet<>();
        Deque<Long> stack = new ArrayDeque<>();
        stack.push(managerId);
        visited.add(managerId);

        while (!stack.isEmpty()) {
            Long currentId = stack.pop();
            Employee currEmployee = employeeRepository.findById(currentId)
                    .orElseThrow(() -> new EmployeeNotFoundException(currentId));

            List<Employee> subordinates = employeeRepository.findByReportingTo(currEmployee);
            for (Employee sub : subordinates) {
                if (visited.add(sub.getId())) {
                    stack.push(sub.getId());
                }
            }
        }
        visited.remove(managerId);
        return visited;

    }

}
