package com.sellspark.SellsHRMS.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;


import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.*;
import lombok.*;
import lombok.Builder;


@Entity
@Table(name = "tbl_epic")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Epic {

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

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EpicStatus status;

    // @OneToMany(mappedBy = "epic", cascade = CascadeType.ALL)
    // @Builder.Default
    // private List<Task> tasks = new ArrayList<>();

    @Builder.Default
    private boolean isActive = true;

    public enum EpicStatus { PLANNING, IN_PROGRESS, COMPLETED, CANCELLED }
}

