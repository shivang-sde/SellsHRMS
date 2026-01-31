package com.sellspark.SellsHRMS.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_user", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // organisation may be SYSTEM org for SuperAdmin users
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    // optional employee link for employee accounts
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String changePasswordHash;
    private LocalDateTime changePasswordDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_role_id")
    private Role orgRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "system_role", length = 30)
    private SystemRole systemRole; // SUPER_ADMIN, ORG_ADMIN, EMPLOYEE, SYSTEM_ACCOUNT

    private LocalDateTime lastLogin;

    @Builder.Default
    private Boolean isActive = true;

    @PrePersist
    public void onCreate() {
        if (isActive == null)
            isActive = true;
    }

    // enums

    public enum SystemRole {
        SUPER_ADMIN,
        ORG_ADMIN,
        EMPLOYEE,
        SYSTEM_ACCOUNT
    }
}
