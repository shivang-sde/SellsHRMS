
package com.sellspark.SellsHRMS.entity;


import java.time.LocalDate;
import java.time.LocalDateTime;



import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
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
    @Column(unique = true)
    private String domain;
    private String logoUrl;
    
   

    private String contactEmail;
    private String contactPhone;

    private String address;
    private String country;
    private String countryCode; // ISO country code (PH, IN, US, etc.)
    private String currencyCode; // default currency for org
    private String timeZone;

    //  @OneToMany(mappedBy = "organisation")
    // private List<WorkLocation> workLocations;

    private String pan;
    private String tan;


    private LocalDate validity;
    private Integer maxEmployees;

    @Column(name = "licence_key")
    private String licenceKey;

    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "suspended_reason")
    private String suspendedReason;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private SubscriptionStatus subscriptionStatus;

    @OneToOne(mappedBy = "organisation", fetch = FetchType.LAZY)
    @JsonManagedReference
    private OrganisationAdmin orgAdmin;

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