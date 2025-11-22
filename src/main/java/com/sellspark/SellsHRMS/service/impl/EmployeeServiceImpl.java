package com.sellspark.SellsHRMS.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sellspark.SellsHRMS.dto.EmployeeDTO;
import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.User;
import com.sellspark.SellsHRMS.repository.DepartmentRepository;
import com.sellspark.SellsHRMS.repository.DesignationRepository;
import com.sellspark.SellsHRMS.repository.EmployeeRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.service.EmployeeService;
import com.sellspark.SellsHRMS.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepo;
    private final OrganisationRepository orgRepo;
    private final DepartmentRepository departmentRepo;
    private final DesignationRepository designationRepo;
    private final UserService userService;

    @Override
    @Transactional
    public Employee create(EmployeeDTO dto) {
        userService.createUser(dto.getEmail(), dto.getPassword(), "EMPLOYEE", dto.getOrganisationId());

        Employee employee = new Employee();
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setGender(dto.getGender());
        employee.setDateOfJoining(dto.getDateOfJoining());
        employee.setDob(dto.getDob());
        employee.setAddress(dto.getAddress());
        employee.setCity(dto.getCity());
        employee.setState(dto.getState());
        employee.setCountry(dto.getCountry());
        employee.setPincode(dto.getPincode());
        employee.setEmploymentType(dto.getEmploymentType());
        employee.setStatus(dto.getStatus());

        // FK references
        employee.setOrganisation(
                orgRepo.findById(dto.getOrganisationId())
                        .orElseThrow(() -> new RuntimeException("Organisation not found")));

        if (dto.getDepartmentId() != null) {
            employee.setDepartment(
                    departmentRepo.findById(dto.getDepartmentId())
                            .orElseThrow(() -> new RuntimeException("Department not found")));
        }

        if (dto.getDesignationId() != null) {
            employee.setDesignation(
                    designationRepo.findById(dto.getDesignationId())
                            .orElseThrow(() -> new RuntimeException("Designation not found")));
        }

        if (dto.getManagerId() != null) {
            employee.setManager(
                    employeeRepo.findById(dto.getManagerId())
                            .orElseThrow(() -> new RuntimeException("Manager not found")));
        }

        employee.setEmployeeCode("EMP-" + System.currentTimeMillis());

        return employeeRepo.save(employee);

    }

    // @Override
    // public Employee create(Employee employee) {
    // return employeeRepo.save(employee);
    // }

    @Override
    public Optional<Employee> getById(Long id) {
        return employeeRepo.findById(id);
    }

    @Override
    public List<Employee> getAll() {
        return employeeRepo.findAll();
    }

    @Override
    public List<Employee> getByOrganisationId(Long orgId) {
        return employeeRepo.findByOrganisation(orgRepo.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organisation not found by orgId: " + orgId)));
    }

    @Override
    public List<Employee> getByOrganisation(Organisation organisation) {
        return employeeRepo.findAll().stream()
                .filter(e -> e.getOrganisation().getId().equals(organisation.getId()))
                .toList();
    }

    @Override
    public Employee update(Long id, EmployeeDTO dto) {

        return employeeRepo.findById(id)
                .map(emp -> {

                    emp.setFirstName(dto.getFirstName());
                    emp.setLastName(dto.getLastName());
                    emp.setPhone(dto.getPhone());
                    emp.setGender(dto.getGender());
                    emp.setDob(dto.getDob());
                    emp.setSalary(dto.getSalary());
                    emp.setEmploymentType(dto.getEmploymentType());
                    emp.setStatus(dto.getStatus());

                    emp.setAddress(dto.getAddress());
                    emp.setCity(dto.getCity());
                    emp.setState(dto.getState());
                    emp.setCountry(dto.getCountry());
                    emp.setPincode(dto.getPincode());

                    if (dto.getDepartmentId() != null)
                        emp.setDepartment(departmentRepo.findById(dto.getDepartmentId())
                                .orElseThrow(() -> new RuntimeException("Department not found")));

                    if (dto.getDesignationId() != null)
                        emp.setDesignation(designationRepo.findById(dto.getDesignationId())
                                .orElseThrow(() -> new RuntimeException("Designation not found")));

                    if (dto.getManagerId() != null)
                        emp.setManager(employeeRepo.findById(dto.getManagerId())
                                .orElseThrow(() -> new RuntimeException("Manager not found")));

                    return employeeRepo.save(emp);
                })
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    @Override
    public void delete(Long id) {
        employeeRepo.deleteById(id);
    }
}
