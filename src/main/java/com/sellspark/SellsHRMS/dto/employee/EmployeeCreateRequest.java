package com.sellspark.SellsHRMS.dto.employee;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class EmployeeCreateRequest {

    // PERSONAL
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String gender;

    private String personalEmail;
    private String phone;
    private String alternatePhone;
    private String fatherName;
    private String nationality;
    private String maritalStatus;
    private String referenceName;
    private String referencePhone;

    // ADDRESS
    private AddressDTO localAddress;
    private AddressDTO permanentAddress;

    // PHOTO UPLOAD
    private MultipartFile photo;

    // COMPANY
    private String employeeCode;
    private LocalDate dateOfJoining;
    private LocalDate dateOfExit;
    private String employmentType;
    private String status;

    private Long organisationId;
    private Long departmentId;
    private Long designationId;
    private Long reportingToId;
    private Long shiftId;

    // private Long roleId;


    // private String dataVisibility; // "SELF", "TEAM", or "ORG"


    // ACCOUNT (LOGIN)
    private String workEmail;
    private String password;
}
