package com.sellspark.SellsHRMS.entity;



import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "tbl_task_label")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaskLabel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    // @ManyToMany(mappedBy = "labels")
    // private List<Task> tasks = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;
}
