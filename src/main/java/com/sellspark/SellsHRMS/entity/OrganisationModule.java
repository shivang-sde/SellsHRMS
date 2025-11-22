package com.sellspark.SellsHRMS.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_organisation_module", uniqueConstraints = @UniqueConstraint(columnNames = { "organisation_id",
        "module_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganisationModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id")
    private Module module;
}
