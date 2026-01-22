package com.sellspark.SellsHRMS.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_ticket", indexes = {
    @Index(name = "idx_ticket_project", columnList = "project_id"),
    @Index(name = "idx_ticket_status", columnList = "status")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private Employee createdBy;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    Organisation organisation;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TicketStatus status;

     // Planned timeline
    private LocalDate startDate;
    private LocalDate endDate;
    
      // Actual lifecycle timestamps
    private LocalDate assignedAt;
    private LocalDate actualStartDate;
    private LocalDate actualCompletionDate;


    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Many employees can work on a ticket
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "tbl_ticket_assignees",
        joinColumns = @JoinColumn(name = "ticket_id"),
        inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    @Builder.Default
    private List<Employee> assignees = new ArrayList<>();


    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<TicketAttachment> attachments = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = TicketStatus.OPEN;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();

        if (status == TicketStatus.IN_PROGRESS && actualStartDate == null) {
            actualStartDate = LocalDate.now();
        }

        if (status == TicketStatus.COMPLETED && actualCompletionDate == null) {
            actualCompletionDate = LocalDate.now();
        }
    }

    public enum TicketStatus {
    OPEN,           // Ticket created, not started yet
    ASSIGNED,       // Assigned but employee not started
    IN_PROGRESS,    // Employee started working
    ON_HOLD,        // Work paused (waiting on something)
    COMPLETED,      // Employee completed work
    CANCELLED       // Ticket closed without completion 
    }

}
