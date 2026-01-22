package com.sellspark.SellsHRMS.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tbl_leave")
public class Leave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    private LocalDate startDate;
    private DayBreakdown startDayBreakdown;

    private LocalDate endDate;
    private DayBreakdown endDayBreakdown;
    private String reason;

    @Column(name = "leave_days")
    private Double leaveDays; // fractional (0.5, 1.0)

    // @Column(name = "financial_year", length = 20)
    // private String financialYear; // e.g., FY2025-26

    @Column(name = "leave_year", length = 20)
    private String leaveYear; // e.g., 2024

    @Column(name = "applied_on")
    private LocalDate appliedOn;

    @Column(name = "approved_on")
    private LocalDate approvedOn;

    private String approverRemarks;

    // @Builder.Default
    // private Boolean isEncashment = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Employee approvedBy;

    @Enumerated(EnumType.STRING)
    private LeaveStatus leaveStatus;

    @Enumerated(EnumType.STRING)
    private LeaveSource source; // EMPLOYEE_APPLY, SYSTEM_AUTO, HR_MANUAL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_summary_id")
    private AttendanceSummary attendanceSummary;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum LeaveStatus {
        PENDING, APPROVE, CANCELED, REJECTED
    }

    public enum LeaveSource {
        EMPLOYEE_APPLY, SYSTEM_AUTO, HR_MANUAL
    }

    public enum DayBreakdown {
        FULL_DAY,
        FIRST_HALF,
        SECOND_HALF
    }
}
