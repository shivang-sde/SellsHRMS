package com.sellspark.SellsHRMS.dto.employee;

import lombok.Data;

@Data
public class EmployeeListDTO {
    private Long id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String email;

    private String department;
    private String designation;
    private String status;
}
