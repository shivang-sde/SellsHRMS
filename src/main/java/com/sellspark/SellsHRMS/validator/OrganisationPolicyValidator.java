package com.sellspark.SellsHRMS.validator;

import org.springframework.stereotype.Component;

import com.sellspark.SellsHRMS.dto.organisation.OrganisationPolicyDTO;

@Component
public class OrganisationPolicyValidator {

    public void validate(OrganisationPolicyDTO dto) {
        if (dto.getOfficeStart() != null && dto.getOfficeClosed() != null) {
            if (dto.getOfficeClosed().isBefore(dto.getOfficeStart())) {
                throw new IllegalArgumentException("Office closing time cannot be before opening time");
            }
        }

        if (dto.getStandardDailyHours() != null && dto.getStandardDailyHours() <= 0) {
            throw new IllegalArgumentException("Daily hours must be greater than 0");
        }

        if (dto.getWeeklyHours() != null && dto.getWeeklyHours() < dto.getStandardDailyHours()) {
            throw new IllegalArgumentException("Weekly hours cannot be less than daily hours");
        }

        if (dto.getAutoPunchOutTime() != null && dto.getOfficeClosed() != null) {
            if (dto.getAutoPunchOutTime().isBefore(dto.getOfficeClosed())) {
                throw new IllegalArgumentException("Auto punch-out cannot be before office closing time");
            }
        }

        if (dto.getMaxWorkHoursBeforeAutoPunchOut() != null &&
                dto.getMaxWorkHoursBeforeAutoPunchOut() < 4) {
            throw new IllegalArgumentException("Max work hours must be reasonable (>= 4)");
        }

        if (dto.getWeekOffDays() != null && dto.getWeekOffDays().isEmpty()) {
            throw new IllegalArgumentException("At least one week off day must be selected");
        }
    }
}