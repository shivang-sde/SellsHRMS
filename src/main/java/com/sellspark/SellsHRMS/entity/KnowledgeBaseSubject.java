package com.sellspark.SellsHRMS.entity;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_kb_subject")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KnowledgeBaseSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KnowledgeBaseTopic> topics;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() { createdAt = LocalDateTime.now(); }
    @PreUpdate
    public void onUpdate() { updatedAt = LocalDateTime.now(); }
}
