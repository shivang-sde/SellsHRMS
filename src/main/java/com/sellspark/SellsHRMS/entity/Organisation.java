
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
import jakarta.persistence.Transient;
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

    @Column(name = "emp_prefix", length = 10, unique = true)
    private String empPrefix;

    @Builder.Default
    private Integer padding = 3;

    @Builder.Default
    @Column(name = "emp_seq")
    private Integer empSequence = 0; // stores last used number

    private String address;
    private String country;
    private String countryCode; // ISO country code (PH, IN, US, etc.)
    private String currencyCode; // default currency for org
    private String timeZone;

    // @OneToMany(mappedBy = "organisation")
    // private List<WorkLocation> workLocations;

    private String aadhar;
    private String pan;
    private String tan;
    private String gst;

    private String aadharUrl;
    private String panUrl;
    private String tanUrl;
    private String gstUrl;

    private boolean isPanVerified;
    private boolean isTanVerified;
    private boolean isGstVerified;
    private boolean isAadharVerified;

    // Aadhaar photo extracted from Sandbox API response
    private String aadhaarPhotoUrl;

    // Sandbox API transaction IDs for audit trail
    @Column(name = "pan_txn_id")
    private String panTransactionId;

    @Column(name = "aadhaar_txn_id")
    private String aadhaarTransactionId;

    @Column(name = "gst_txn_id")
    private String gstTransactionId;

    // Resumable verification email token
    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "verification_token_expiry")
    private LocalDateTime verificationTokenExpiry;

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

    /**
     * Returns the count of verified documents (PAN, Aadhaar, GST, TAN).
     * Used by OrganisationAccessFilter to gate login access.
     */
    @Transient
    public int getVerifiedDocumentCount() {
        int count = 0;
        if (isPanVerified) count++;
        if (isAadharVerified) count++;
        if (isGstVerified) count++;
        if (isTanVerified) count++;
        return count;
    }

}