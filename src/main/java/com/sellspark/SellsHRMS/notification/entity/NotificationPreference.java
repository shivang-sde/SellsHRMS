package com.sellspark.SellsHRMS.notification.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.sellspark.SellsHRMS.entity.Organisation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tbl_notification_preference", uniqueConstraints = @UniqueConstraint(name = "uk_org_event", columnNames = {
        "org_id", "event_id" }))
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Use a real relation to Organisation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private Organisation organisation;

    // Link directly to the Event object instead of a String ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private NotificationEvent notificationEvent;

    @Builder.Default
    @Column(name = "email_enabled")
    private Boolean emailEnabled = false;

    @CreationTimestamp
    private LocalDateTime createdAt;
}