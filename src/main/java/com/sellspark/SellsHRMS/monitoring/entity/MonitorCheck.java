package com.sellspark.SellsHRMS.monitoring.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "monitor_checks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonitorCheck {

    @Id
    private String id; // UUID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_id", nullable = false)
    private MonitorUrl url;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "response_time")
    private Integer responseTime; // ms

    @Column(name = "is_up", nullable = false)
    private Boolean isUp;

    @Column(columnDefinition = "TEXT")
    private String error;

    @Column(name = "checked_at", nullable = false)
    private LocalDateTime checkedAt;

    @PrePersist
    protected void onCreate() {
        id = UUID.randomUUID().toString();
        checkedAt = LocalDateTime.now();
    }
}