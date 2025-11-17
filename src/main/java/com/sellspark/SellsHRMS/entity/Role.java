package com.sellspark.SellsHRMS.entity;

import jakarta.persistence.*;
import lombok.*;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @Column(nullable = false)
    private String name; // e.g. ADMIN, HR, MANAGER

    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tbl_role_permission", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions;
}
