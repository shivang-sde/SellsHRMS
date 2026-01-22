package com.sellspark.SellsHRMS.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.Builder.Default;

import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tbl_role", uniqueConstraints = @UniqueConstraint(columnNames = { "organisation_id", "name" }))

public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g. ADMIN, HR, MANAGER
    private String description;

    @Builder.Default
    private boolean isActive = true;

    @OneToOne(mappedBy = "role", fetch = FetchType.LAZY)
    private Designation designation;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tbl_role_permission", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false) // id = 1 for SYSTEM org
    private Organisation organisation;
}
