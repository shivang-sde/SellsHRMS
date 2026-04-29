package com.sellspark.SellsHRMS.notification.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_notification_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String module; // e.g. "EMPLOYEE", "LEAVE", "ATTENDANCE", "PAYROLL", "ORGANIZATION"

    @Column(length = 50, nullable = false, unique = true)
    private String eventCode; // e.g. "EMPLOYEE_CREATED", "LEAVE_APPLIED", "LEAVE_APPROVED", "LEAVE_REJECTED",
                              // "ATTENDANCE_PUNCHED", "ATTENDANCE_APPROVED", "ATTENDANCE_REJECTED",
                              // "PAYROLL_GENERATED", "PAYROLL_APPROVED", "PAYROLL_REJECTED",
                              // "ORGANIZATION_CREATED", "ORGANIZATION_APPROVED", "ORGANIZATION_REJECTED"

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
