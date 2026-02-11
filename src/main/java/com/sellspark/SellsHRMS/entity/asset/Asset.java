package com.sellspark.SellsHRMS.entity.asset;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.Organisation;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_asset", uniqueConstraints = @UniqueConstraint(columnNames = { "organisation_id", "asset_code" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "asset_code", nullable = false)
    private String assetCode;

    @Column(nullable = false)
    private String name;

    private String description;

    private LocalDate purchaseDate;
    private Double cost;

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_condition", length = 20)
    @Builder.Default
    private AssetCondition condition = AssetCondition.NEW;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private AssetStatus status = AssetStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private AssetCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private Employee assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum AssetCondition {
        NEW, GOOD, FAIR, DAMAGED
    }

    public enum AssetStatus {
        AVAILABLE, ASSIGNED, UNDER_MAINTENANCE, DISPOSED
    }
}
