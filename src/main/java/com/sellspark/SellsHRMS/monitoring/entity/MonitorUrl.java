package com.sellspark.SellsHRMS.monitoring.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "monitor_urls", uniqueConstraints = @UniqueConstraint(columnNames = { "organisation_id", "url" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonitorUrl {

    @Id
    private String id; // UUID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 1000)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestMethod method; // GET, POST, HEAD

    @Column(name = "check_interval", nullable = false)
    private Integer checkInterval; // seconds, default 300

    @Column(nullable = false)
    private Integer timeout; // seconds, default 30

    @Column(name = "expected_status", length = 20)
    private String expectedStatus; // "2xx", "200", etc.

    @Column(columnDefinition = "json")
    private String headers; // JSON string

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false)
    private Status currentStatus = Status.pending;

    @Column(name = "last_checked_at")
    private LocalDateTime lastCheckedAt;

    @Column(name = "last_response_time")
    private Integer lastResponseTime; // ms

    @Column(name = "last_status_code")
    private Integer lastStatusCode;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @Builder.Default
    @Column(name = "uptime_percentage")
    private BigDecimal uptimePercentage = BigDecimal.valueOf(100.00);

    @Builder.Default
    @Column(name = "consecutive_failures", nullable = false)
    private Integer consecutiveFailures = 0;

    @Builder.Default
    @Column(name = "failure_threshold", nullable = false)
    private Integer failureThreshold = 3;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Enums
    public enum RequestMethod {
        GET, POST, HEAD
    }

    public enum Status {
        up, down, pending
    }

    @PrePersist
    protected void onCreate() {
        id = UUID.randomUUID().toString();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}