package com.sellspark.SellsHRMS.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "tbl_tasks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column(name = "task_key", length = 50)
    // private String taskKey; // e.g., "HRMS-101"

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

     // ✅ For personal/reminder tasks only
    private LocalDateTime reminderAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TaskStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    // RELATIONSHIPS
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project; // nullable → supports independent tasks


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private Employee assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private Employee reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;


    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    @Builder.Default
    private List<TaskAttachment> attachments = new ArrayList<>();

    @Column(name = "is_self_task")
    @Builder.Default
    private Boolean isSelfTask = false;

    @Column(name = "reminder_enabled")
    private Boolean reminderEnabled;


    @Builder.Default
    private Boolean isActive = true;

    // AUDIT
    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private Employee createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isActive == null) isActive = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    public enum TaskPriority { LOW, MEDIUM, HIGH, URGENT }
    public enum TaskStatus { BACKLOG, TO_DO, IN_PROGRESS, REVIEW, DONE, REMINDER }
   
}


 