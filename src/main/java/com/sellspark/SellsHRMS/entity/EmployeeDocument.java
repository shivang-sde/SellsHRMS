package com.sellspark.SellsHRMS.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_employee_document",
       uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "document_type"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDocument {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "document_type", length = 50, nullable = false)
    private String documentType; // e.g. RESUME, OFFER, JOINING, AGREEMENT, OTHER

    @Column(name = "file_url", length = 1024)
    private String fileUrl;      // served URL, like /uploads/{empId}/{file}

    @Column(name = "external_url", length = 1024)
    private String externalUrl;  // Dropbox/Google link

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @Column(name = "verified")
    private Boolean verified = false;

    // other metadata columns can be added (size, mime, original filename)
    @Column(name = "original_filename", length = 512)
    private String originalFilename;

    @Column(name = "content_type", length = 128)
    private String contentType;
}
