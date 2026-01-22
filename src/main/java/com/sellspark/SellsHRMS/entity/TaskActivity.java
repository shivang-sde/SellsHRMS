package com.sellspark.SellsHRMS.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
  name = "tbl_task_activity",
  indexes = {
    @Index(name = "idx_task_activity_task", columnList = "task_id"),
    @Index(name = "idx_task_activity_employee", columnList = "employee_id"),
    @Index(name = "idx_task_activity_type", columnList = "activityType")
  }
)

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaskActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Task this activity belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    // Employee who performed the action
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // Type of activity
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ActivityType activityType;

    private String oldValue;
    private String newValue;

    // Optional reference to attachment or comment involved
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id")
    private TaskAttachment attachment;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum ActivityType {
        TASK_CREATED,
        TASK_UPDATED,
        STATUS_CHANGED,
        PRIORITY_CHANGED,    
        ATTACHMENT_UPLOADED,
        ATTACHMENT_REMOVED,
        REMINDER_TOGGLE,
        DESCRIPTION_CHANGED,
        REMINDER_SET
    }
}
