package com.sellspark.SellsHRMS.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_kb_topic")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KnowledgeBaseTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 5000)
    private String content;

    private String attachmentUrl;

    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private KnowledgeBaseSubject subject;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() { createdAt = LocalDateTime.now(); }
    @PreUpdate
    public void onUpdate() { updatedAt = LocalDateTime.now(); }
}
