package com.sellspark.SellsHRMS.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_sprint")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Sprint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    @Column(nullable = false, length = 200)
    private String name;

    private LocalDate startDate;
    private LocalDate endDate;

    @Builder.Default
    private boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SprintStatus status;

    // @OneToMany(mappedBy = "sprint", cascade = CascadeType.ALL)
    // @Builder.Default
    // private List<Task> tasks = new ArrayList<>();

    public enum SprintStatus { PLANNED, ACTIVE, COMPLETED, CANCELLED }
}
