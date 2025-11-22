package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.dto.EmployeeDTO;
import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.service.EmployeeService;
import com.sellspark.SellsHRMS.service.OrganisationAdminService;
import com.sellspark.SellsHRMS.service.OrganisationService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/org-admin")
@RequiredArgsConstructor
public class OrgAdminRestController {

    private final OrganisationService organisationService;
    private final EmployeeService employeeService;
    private final OrganisationAdminService orgAdminService;

    // --- UPDATE OWN ORGANISATION ---

    @PutMapping("/organisation/{id}")
    public Organisation updateOrg(@PathVariable Long id, @RequestBody Organisation org) {
        return organisationService.update(id, org);
    }

    // --- EMPLOYEE CRUD ---

    @PostMapping("/employee")
    public Employee createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        return employeeService.create(employeeDTO);
    }

    @PutMapping("/employee/{id}")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody EmployeeDTO updatedDTO) {
        return employeeService.update(id, updatedDTO);
    }

    @GetMapping("/employee/{id}")
    public Employee getEmployee(@PathVariable Long id) {
        return employeeService.getById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    @DeleteMapping("/employee/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.delete(id);
    }

    @GetMapping("/employees/{orgId}")
    public List<Employee> getEmployeesByOrg(@PathVariable Long orgId) {
        Organisation org = organisationService.getById(orgId)
                .orElseThrow(() -> new RuntimeException("Organisation not found"));
        return employeeService.getByOrganisation(org);
    }

    // --- DASHBOARD ---

    @GetMapping("/stats/employees/{orgId}")
    public int getEmployeeCount(@PathVariable Long orgId) {
        return orgAdminService.getEmployeeCount(orgId);
    }
}
