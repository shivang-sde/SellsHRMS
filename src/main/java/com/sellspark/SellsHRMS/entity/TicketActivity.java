package com.sellspark.SellsHRMS.entity;

import java.time.LocalDateTime;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "tbl_ticket_activity")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ActivityType type;

    

    @Column(length = 1000)
    private String description;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() { createdAt = LocalDateTime.now(); }

    public enum ActivityType {
        TICKET_CREATED,
        TICKET_UPDATED,
        STATUS_CHANGED,
        ASSIGNEE_ADDED,
        ASSIGNEES_UPDATED,
        ASSIGNEE_REMOVED,
        PICKED,
        TASK_ADDED,
        TASK_COMPLETED,
        ATTACHMENT_UPLOADED,
        TICKET_DELETED,
        TICKET_PAUSED,
        NOTE_ADDED
    }
}
