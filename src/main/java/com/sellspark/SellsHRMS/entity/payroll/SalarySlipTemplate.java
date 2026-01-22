package com.sellspark.SellsHRMS.entity.payroll;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.UpdateTimestamp;

import com.sellspark.SellsHRMS.entity.Organisation;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_salary_slip_template")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalarySlipTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;  

    @Column(name = "template_name", nullable = false, length = 255)
    private String templateName;

    @Lob
    @Column(name = "template_html", nullable = false, columnDefinition = "TEXT")
    private String templateHtml;

    @Lob
    @Column(name = "config_json", nullable = false, columnDefinition = "TEXT")
    private String configJson;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}