package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.dto.EmployeeDTO;
import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.service.EmployeeService;
import com.sellspark.SellsHRMS.service.OrganisationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeRestController {

    private final EmployeeService employeeService;
    private final OrganisationService organisationService;

    // --------------------------
    // CREATE EMPLOYEE
    // --------------------------
    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        Employee saved = employeeService.create(employeeDTO);
        return ResponseEntity.ok(saved);
    }

    // --------------------------
    // UPDATE EMPLOYEE
    // --------------------------
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(
            @PathVariable Long id,
            @RequestBody EmployeeDTO updatedDTO) {

        Employee saved = employeeService.update(id, updatedDTO);
        return ResponseEntity.ok(saved);
    }

    // --------------------------
    // GET BY ID
    // --------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployee(@PathVariable Long id) {
        Optional<Employee> employeeOpt = employeeService.getById(id);

        if (employeeOpt.isPresent()) {
            // Returns 200 OK with an Employee body
            return ResponseEntity.ok(employeeOpt.get());
        } else {
            // Returns 404 Not Found (no body needed for 404 typically)
            return ResponseEntity.notFound().build();

            /*
             * If you MUST have a string body for 404, use this:
             * return ResponseEntity.status(404).body("Employee not found");
             */
        }
    }

    // --------------------------
    // DELETE EMPLOYEE
    // --------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.ok().build();
    }

    // --------------------------
    // LIST EMPLOYEES BY ORG
    // --------------------------
    @GetMapping("/organisation/{orgId}")
    public ResponseEntity<?> getEmployeesByOrg(@PathVariable Long orgId) {

        Organisation org = organisationService.getById(orgId)
                .orElse(null);

        if (org == null) {
            return ResponseEntity.status(404)
                    .body("Organisation not found");
        }

        List<Employee> employees = employeeService.getByOrganisation(org);
        return ResponseEntity.ok(employees);
    }
}
