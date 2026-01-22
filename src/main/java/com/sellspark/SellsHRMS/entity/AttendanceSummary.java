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
@Table(name = "tbl_attendance_summary",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"employee_id", "attendance_date"})},
       indexes = {
           @Index(name = "idx_org_date", columnList = "organisation_id, attendance_date"),
           @Index(name = "idx_emp_date", columnList = "employee_id, attendance_date"),
           @Index(name = "idx_org_date_status", columnList = "organisation_id, attendance_date, status")
       })
public class AttendanceSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;  

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee; 

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate; 

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private AttendanceStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "punch_record_id")
    private PunchInOut punchRecord; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_id")
    private Leave leave;

    @Column(name = "effective_punch_in")
    private LocalDateTime effectivePunchIn;

    @Column(name = "effective_punch_out")
    private LocalDateTime effectivePunchOut;

    @Column(name = "work_hours")
    private Double workHours;

    @Column(name = "overtime_hours")
    private Double overtimeHours;

    @Column(name = "is_late")
    @Builder.Default
    private Boolean isLate = false;

    @Column(name = "is_early_out")
    @Builder.Default
    private Boolean isEarlyOut = false;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private AttendanceSource source;

    @Column(name = "remarks", length = 500)
    private String remarks;

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

    public enum AttendanceStatus {
        ABSENT, PRESENT, HALF_DAY, SHORT_DAY, ON_LEAVE, HOLIDAY, WEEK_OFF, PENDING, WFH, ON_DUTY
    }

    public enum AttendanceSource {
        AUTO_SYSTEM, PUNCH_SYSTEM, REGULARIZATION, MANUAL_HR, BIOMETRIC, LEAVE_SYSTEM
    }
}
