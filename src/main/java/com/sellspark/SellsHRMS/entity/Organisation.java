
package com.sellspark.SellsHRMS.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_organisation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String domain;
    private String logoUrl;

    private String contactEmail;
    private String contactPhone;

    private String adress;
    private String country;

    private String pan;
    private String tan;

    private Integer maxEmployees;
    
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus subscriptionStatus;

    @Builder.Default
    private Boolean isActive = true;

    // TODO: add Plans related fields or create seprate entity plam(id, name, price,
    // max_user)

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum SubscriptionStatus {
        ACTIVE, EXPIRED, SUSPENDED
    }

}