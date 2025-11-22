package com.sellspark.SellsHRMS.dto;

import java.time.LocalDate;

import com.sellspark.SellsHRMS.entity.Employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EmployeeDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;

    private Employee.Gender gender;
    private LocalDate dob;
    private LocalDate dateOfJoining;

    private Double salary;
    private String address;
    private String city;
    private String state;
    private String country;
    private String pincode;

    private Employee.EmploymentType employmentType;
    private Employee.EmployeeStatus status;

    private Long departmentId;
    private Long designationId;
    private Long managerId;
    private Long organisationId;
}
