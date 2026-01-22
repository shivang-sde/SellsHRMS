package com.sellspark.SellsHRMS.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_task_comment")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaskComment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "task_id", nullable = false)
    // private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false, length = 2000)
    private String comment;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //  @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    // @Builder.Default
    // private List<TaskAttachment> attachments = new ArrayList<>();


    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}