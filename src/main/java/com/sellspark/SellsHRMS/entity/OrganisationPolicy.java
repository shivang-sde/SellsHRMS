package com.sellspark.SellsHRMS.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tbl_organisation_policy")
public class OrganisationPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @Builder.Default
    private Integer financialYearStartMonth = 4; // default April
    @Builder.Default
    private Integer financialYearStartDay = 1;   // default 1st day
    @Builder.Default
    private Integer leaveYearStartMonth = 1; // default January
    @Builder.Default
    private Integer leaveYearStartDay = 1;   // default 1st day


   private Integer salaryCycleStartDay; // e.g. 1
    private Integer cycleDuration;       // e.g. 30
    private Integer payslipGenerationOffsetDays; // e.g. 5 days after cycle end


    // Attendance & work-hour settings
    @Builder.Default
    private Double standardDailyHours = 8.0;
    @Builder.Default
    private Double weeklyHours = 40.0;
    @Builder.Default
        private Double monthlyHours = 160.0;    // e.g., 160 hours/month
    @Builder.Default
    private LocalTime autoPunchTime = LocalTime.of(23, 59);
    @Builder.Default
    private Integer lateGraceMinutes = 10;
    @Builder.Default
    private Integer earlyOutGraceMinutes = 10;

    // Payroll dependencies
    @Builder.Default
    private Boolean overtimeAllowed = true;
    @Builder.Default
    private Double overtimeMultiplier = 1.5;
    @Builder.Default
    private Double minMonthlyHours = 160.0;
    @Builder.Default
    private Boolean flexibleHourModelEnabled = false;

    // Leave policies
    @Builder.Default
    private Boolean carryForwardEnabled = true;
    @Builder.Default
    private Boolean encashmentEnabled = true;

    private String additionalNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
