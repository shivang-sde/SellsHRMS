package com.sellspark.SellsHRMS.dto.organisation;

import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Builder;
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
    private Integer financialYearStartMonth;
    private Integer financialYearStartDay;
    private Integer leaveYearStartMonth;
    private Integer leaveYearStartDay;
    private Double standardDailyHours;
    private Double weeklyHours;
    private LocalTime autoPunchTime;
    private Integer lateGraceMinutes;
    private Integer earlyOutGraceMinutes;
    private Boolean overtimeAllowed;
    private Double overtimeMultiplier;
    private Double minMonthlyHours;
    private Boolean flexibleHourModelEnabled;
    private Boolean carryForwardEnabled;
    private Boolean encashmentEnabled;
    private String additionalNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}