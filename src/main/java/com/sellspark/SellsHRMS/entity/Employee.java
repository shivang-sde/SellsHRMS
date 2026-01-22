package com.sellspark.SellsHRMS.entity;

import java.time.LocalDate;
import java.util.List;

import com.sellspark.SellsHRMS.entity.payroll.SalaryStructure;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;


@Entity
@Table(name = "tbl_employee")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Employee {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ------------- BASIC IDENTIFIERS ----------------
    @Column(unique = true, nullable = false)
    private String employeeCode;     // Human-readable employee ID

    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String email;            // official email

    private String phone;            // primary phone

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate dob;

    // ------------- EMPLOYMENT INFO ----------------
    private LocalDate dateOfJoining;
    private LocalDate dateOfExit;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", length = 20, nullable = false)
    private EmploymentType employmentType;   // FullTime / PartTime / Contract / Intern

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private EmployeeStatus status;           // Active / Suspended / Terminated / Inactive

//     @Enumerated(EnumType.STRING)
// @Column(name = "data_visibility", length = 20, )
// @Builder.Default
// private DataVisibility dataVisibility = DataVisibility.SELF;


    private Double salary;                   // Quick reference CTC or basic salary


    // ------------- EMPLOYMENT LIFECYCLE ----------------
private LocalDate probationEndDate;   // e.g., 3 or 6 months after joining
private LocalDate confirmationDate;   // official date of confirmation
private Integer noticePeriodDays;     // applicable during exit

@Enumerated(EnumType.STRING)
@Column(length = 20)
private ServiceStage serviceStage;   // ACTIVE, PROBATION, NOTICE, LEAVE_WITHOUT_PAY, SABBATICAL


    // ------------- SOFT DELETE FLAG ----------------
    @Builder.Default
    private boolean deleted = false;

    // ------------- ADDRESS (EMBEDDED) -------------
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "line1", column = @Column(name = "local_line1")),
            @AttributeOverride(name = "line2", column = @Column(name = "local_line2")),
            @AttributeOverride(name = "city", column = @Column(name = "local_city")),
            @AttributeOverride(name = "state", column = @Column(name = "local_state")),
            @AttributeOverride(name = "country", column = @Column(name = "local_country")),
            @AttributeOverride(name = "pincode", column = @Column(name = "local_pincode"))
    })
    private Address localAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "line1", column = @Column(name = "perm_line1")),
            @AttributeOverride(name = "line2", column = @Column(name = "perm_line2")),
            @AttributeOverride(name = "city", column = @Column(name = "perm_city")),
            @AttributeOverride(name = "state", column = @Column(name = "perm_state")),
            @AttributeOverride(name = "country", column = @Column(name = "perm_country")),
            @AttributeOverride(name = "pincode", column = @Column(name = "perm_pincode"))
    })
    private Address permanentAddress;

    // ------------- RELATIONSHIPS ----------------
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "designation_id")
    private Designation designation; // Job title

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reporting_to_id")
    private Employee reportingTo;

    // @ManyToOne(fetch = FetchType.EAGER)
    // @JoinColumn(name = "role_id")
    // private Role role; // Access control role

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EmployeeBank> bankAccounts = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EmployeeDocument> documents = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EmployeeShift> shifts = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EmployeeSalaryDetail> salaryDetails = new ArrayList<>();


    // @OneToOne(mappedBy = "employee")
    // private SalaryStructure salaryStructure;

    // ---------------- EXTRA PERSONAL INFO ----------------
    private String fatherName;
    private String personalEmail;
    private String alternatePhone;
    private String nationality;
    private String maritalStatus;       // Keep as String (simple, no overengineering)

    private String referenceName;
    private String referencePhone;

    private String photoUrl;            
    private String biometricId;         

    // ------------- ENUMS ----------------
    public enum Gender { MALE, FEMALE, OTHER }

    public enum EmploymentType {
        FULLTIME, PARTTIME, CONTRACT, INTERN, CONSULTANT
    
    }
    public enum EmployeeStatus {
        ACTIVE, INACTIVE, SUSPENDED, TERMINATED
    }

    

    public enum ServiceStage {
    PROBATION,
    CONFIRMED,
    NOTICE,
    LEAVE_WITHOUT_PAY,
    SABBATICAL,
    RETIRED
}

//     public enum DataVisibility {
//     SELF,        // can view only own record
//     TEAM,        // can view direct reports (reporting_to = self)
//     ORG          // can view everyone in org
// }


public boolean isOnProbation() {
    return probationEndDate != null && LocalDate.now().isBefore(probationEndDate);
}

public boolean isConfirmed() {
    return confirmationDate != null && !LocalDate.now().isBefore(confirmationDate);
}




}
