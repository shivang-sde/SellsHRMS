package com.sellspark.SellsHRMS.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_task_attachment")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaskAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Can belong to a task or a comment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileUrl; // or path if stored locally

    @Column(length = 100)
    private String fileType; // e.g., "image/png", "application/pdf"

    @Column(name = "file_size_kb")
    private Double fileSizeKB;

     @Column(length = 500)
    private String description; //

    @Column(length = 1000)
    private String externalLink; // for URLs or shared links (optional)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private Employee uploadedBy;

    private LocalDateTime uploadedAt;

    @PrePersist
    public void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}

