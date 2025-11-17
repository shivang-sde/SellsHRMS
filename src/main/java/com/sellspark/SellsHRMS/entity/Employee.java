package com.sellspark.SellsHRMS.entity;

import java.time.LocalDate;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tbl_employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String employeeCode;
    
    @Column(nullable = false)
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate dob;
    private LocalDate dateOfJoining;
    private LocalDate dateOfExit;

    private Double salary;
    private String address;
    private String city;
    private String state;
    private String country;
    private String pincode;


    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;

    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;



    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designation_id")
    private Designation designation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;



    
    public enum Gender {Male, Female, Other}
    public enum EmploymentType {FullTime,PartTime, Contract, Intern}
    public enum EmployeeStatus {Active, OnLeave, Resigned, Terminated }

}
