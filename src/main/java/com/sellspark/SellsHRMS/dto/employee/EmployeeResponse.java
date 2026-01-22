package com.sellspark.SellsHRMS.dto.employee;

import lombok.Data;

@Data
public class EmployeeResponse {

    private Long id;
    private String employeeCode;
    private String fullName;
    private String email;
    private String phone;
    private String status;
    private String role;
    private String employmentType;
    private String department;
    private String designation;
    private String organisation;
}
