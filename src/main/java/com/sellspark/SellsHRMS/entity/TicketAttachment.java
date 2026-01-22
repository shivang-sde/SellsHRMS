package com.sellspark.SellsHRMS.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "tbl_ticket_attachment")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    private String fileName;
    private String fileUrl;
    private String fileType;
    private Double fileSizeKB;

     // 🆕 NEW FIELD — Attachment Description / Note
    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private Employee uploadedBy;

    private LocalDateTime uploadedAt;

    @PrePersist
    public void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}

