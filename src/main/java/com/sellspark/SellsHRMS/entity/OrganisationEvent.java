package com.sellspark.SellsHRMS.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_organisation_event")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrganisationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    private String location;

    @Enumerated(EnumType.STRING)
    private EventType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() { createdAt = LocalDateTime.now(); }

    @PreUpdate
    public void onUpdate() { updatedAt = LocalDateTime.now(); }

    public enum EventType {
        MEETING, CELEBRATION, TRAINING, CUSTOM
    }
}
