package com.sellspark.SellsHRMS.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_project_attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id", nullable = false)
    private Employee uploadedBy;

    @Column(length = 255)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(length = 1000)
    private String url; // for uploaded file OR external resource link (e.g. YouTube, Google Drive)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AttachmentType type; // FILE, VIDEO_LINK, DOCUMENT_LINK, OTHER

    private LocalDateTime uploadedAt;

    @PrePersist
    public void onCreate() {
        uploadedAt = LocalDateTime.now();
    }

    public enum AttachmentType {
        FILE,
        VIDEO_LINK,
        DOCUMENT_LINK,
        OTHER
    }
}
