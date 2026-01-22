package com.sellspark.SellsHRMS.entity;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tbl_holiday",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"organisation_id", "holiday_date", "holiday_name" })},
       indexes = {@Index(name = "idx_org_date", columnList = "organisation_id, holiday_date")})
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @Column(name = "holiday_date", nullable = false)
    private LocalDate holidayDate;

    @Column(name = "holiday_name", nullable = false, length = 100)
    private String holidayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "holiday_type", length = 20)
    private HolidayType holidayType;

    @Column(name = "is_mandatory")
    @Builder.Default
    private Boolean isMandatory = true;

    @Column(name = "description", length = 500)
    private String description;

    // Optional: branch support
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "branch_id")
    // private Branch branch;

    public enum HolidayType {
        PUBLIC, COMPANY_SPECIFIC, OPTIONAL
    }
}
