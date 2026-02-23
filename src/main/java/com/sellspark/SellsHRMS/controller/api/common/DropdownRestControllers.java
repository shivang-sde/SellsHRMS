package com.sellspark.SellsHRMS.controller.api.common;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sellspark.SellsHRMS.dto.common.DropdownOption;
import com.sellspark.SellsHRMS.service.common.DropdownService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/common/dropdowns")
@RequiredArgsConstructor
public class DropdownRestControllers {

    private final DropdownService dropdownService;

    @GetMapping("/departments/{orgId}")
    public ResponseEntity<List<DropdownOption>> getDepartmentDropdown(@PathVariable Long orgId) {
        List<DropdownOption> departments = dropdownService.getDepartmentDropdown(orgId);
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/designations/{orgId}")
    public ResponseEntity<List<DropdownOption>> getDesignationDropdown(@PathVariable Long orgId) {
        List<DropdownOption> designations = dropdownService.getDesignationDropdown(orgId);
        return ResponseEntity.ok(designations);
    }

    @GetMapping("/employees/{orgId}")
    public ResponseEntity<List<DropdownOption>> getEmployeeDropdown(@PathVariable Long orgId) {
        List<DropdownOption> employees = dropdownService.getEmployeeDropdown(orgId);
        return ResponseEntity.ok(employees);
    }
}
