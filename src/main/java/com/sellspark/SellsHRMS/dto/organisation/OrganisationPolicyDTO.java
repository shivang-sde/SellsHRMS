package com.sellspark.SellsHRMS.dto.organisation;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
public class OrganisationPolicyDTO {

    private Long id;
    private Long organisationId;

    // Financial & Leave
    private Integer financialYearStartMonth;
    private Integer financialYearStartDay;
    private Integer leaveYearStartMonth;
    private Integer leaveYearStartDay;

    // Attendance Core
    private LocalTime officeStart;
    private LocalTime officeClosed;
    private Double standardDailyHours;
    private Double weeklyHours;
    private Double monthlyHours;

    // CRITICAL (Scheduler dependent)
    private LocalTime autoPunchOutTime;
    private Integer maxWorkHoursBeforeAutoPunchOut;

    // Week off (NEW)
    private List<DayOfWeek> weekOffDays;

    // Grace
    private Integer lateGraceMinutes;
    private Integer earlyOutGraceMinutes;

    // Payroll
    private Boolean overtimeAllowed;
    private Double overtimeMultiplier;
    private Double minMonthlyHours;

    // Salary Cycle
    private Integer salaryCycleStartDay;
    private Integer cycleDuration;
    private Integer payslipGenerationOffsetDays;

    // Leave
    private Boolean carryForwardEnabled;
    private Boolean encashmentEnabled;

    private String additionalNotes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}