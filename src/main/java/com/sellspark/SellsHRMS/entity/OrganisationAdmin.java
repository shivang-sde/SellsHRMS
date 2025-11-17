package com.sellspark.SellsHRMS.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_org_admin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganisationAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    private String passwordHash;

    private LocalDateTime lastLogin;

    @Builder.Default
    private Boolean isActive = true;

}
