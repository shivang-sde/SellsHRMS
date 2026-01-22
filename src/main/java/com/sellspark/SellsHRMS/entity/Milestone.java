package com.sellspark.SellsHRMS.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "tbl_milestone")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class Milestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;


    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    private LocalDate targetDate;
    private LocalDate achievedDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MilestoneStatus status;

    // @OneToMany(mappedBy = "milestone", cascade = CascadeType.ALL)
    // @Builder.Default
    // private List<Task> tasks = new ArrayList<>();

    @Builder.Default
    private Boolean isActive = true;

    public enum MilestoneStatus { PLANNED, ACHIEVED, DELAYED }
}