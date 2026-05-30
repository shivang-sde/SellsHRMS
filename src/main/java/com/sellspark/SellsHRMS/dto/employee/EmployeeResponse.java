package com.sellspark.SellsHRMS.dto.employee;

import java.time.LocalDate;

import lombok.Data;

@Data
public class EmployeeResponse {

    private Long id;
    private Long userId;
    private String employeeCode;
    private String fullName;
    private String email;
    private LocalDate dob;
    private LocalDate doj;
    private String photoUrl;
    private String phone;
    private String status;
    private String role;
    private String employmentType;
    private String department;
    private String designation;
    private String organisation;
}
