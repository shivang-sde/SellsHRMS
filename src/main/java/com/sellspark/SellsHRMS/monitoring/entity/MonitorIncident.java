package com.sellspark.SellsHRMS.monitoring.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "monitor_incidents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonitorIncident {

    @Id
    private String id; // UUID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_id", nullable = false)
    private MonitorUrl url;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(length = 500)
    private String cause;

    @Builder.Default
    @Column(name = "is_resolved", nullable = false)
    private Boolean isResolved = false;

    @Builder.Default
    @Column(name = "notification_sent", nullable = false)
    private Boolean notificationSent = false;

    @Builder.Default
    @Column(name = "recovery_notification_sent", nullable = false)
    private Boolean recoveryNotificationSent = false;

    @PrePersist
    protected void onCreate() {
        id = UUID.randomUUID().toString();
        startedAt = LocalDateTime.now();
    }
}