package com.sellspark.SellsHRMS.dto.employee;

import lombok.Data;
import java.util.List;

@Data
public class EmployeeDetailResponse {

    private Long id;
    private String employeeCode;

    private String firstName;
    private String lastName;
    private String fullName;

    private String email;
    private String phone;
    private String alternatePhone;
    private String personalEmail;

    private String gender;
    private String dob;

    private String status;
    private String employmentType;
    private Double salary;

    private String dateOfJoining;
    private String dateOfExit;

    private String department;
    private String designation;
    private String organisation;

    private String role;

    private String reportingToName;

    private AddressDTO localAddress;
    private AddressDTO permanentAddress;

    // Extra personal info
    private String fatherName;
    private String maritalStatus;
    private String nationality;

    private String referenceName;
    private String referencePhone;
    // Child collections
    private List<EmployeeBankRequest> bankAccounts;
    private List<EmployeeDocumentRequest> documents;
    // private List<EmployeeShiftDTO> shifts;

    // Flags
    private boolean deleted;
}

