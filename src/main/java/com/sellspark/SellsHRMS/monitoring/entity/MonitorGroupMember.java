package com.sellspark.SellsHRMS.monitoring.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.sellspark.SellsHRMS.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "monitor_group_members", uniqueConstraints = @UniqueConstraint(columnNames = { "group_id", "user_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonitorGroupMember {

    @Id
    private String id; // UUID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private MonitorGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by", nullable = false)
    private User addedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        id = UUID.randomUUID().toString();
        createdAt = LocalDateTime.now();
    }
}