package com.sellspark.SellsHRMS.entity.payroll;

import java.util.ArrayList;
import java.util.List;

import com.sellspark.SellsHRMS.entity.Organisation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "tbl_statutory_component",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"organisation_id", "code"})
    }
)
public class StatutoryComponent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code; // e.g., "EPF", "ESI", "SSS", "PHILHEALTH"
    private String name; // descriptive name
    private String description;

    // In StatutoryComponent.java
    @Builder.Default
    @OneToMany(mappedBy = "statutoryComponent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StatutoryRule> rules = new ArrayList<>();


    @ManyToOne
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    @Column(name = "country_code", length = 5)
    private String countryCode; // e.g., "IN", "PH"

    @Column(name = "state_code", length = 5)
    private String stateCode; // optional, for state-specific like PT, LWF

    private Boolean includeInCalculation = true;

    @Builder.Default
    private Boolean isActive = true;

}
