package com.sellspark.SellsHRMS.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_device", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "organisation_id", "deviceCode" }),
        @UniqueConstraint(columnNames = "apiKey")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @Column(nullable = false)
    private String deviceCode; // e.g., "Kiosk-Main-Entrance"

    @Column(nullable = false)
    private String name; // Friendly name

    @Column(nullable = false, unique = true)
    private String apiKey;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.ACTIVE;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum Status {
        ACTIVE, INACTIVE
    }
}
