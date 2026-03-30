package com.sellspark.SellsHRMS.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sellspark.SellsHRMS.dto.organisation.OrganisationPolicyDTO;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.OrganisationPolicy;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.repository.OrganisationPolicyRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.service.OrganisationPolicyService;
import com.sellspark.SellsHRMS.validator.OrganisationPolicyValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganisationPolicyServiceImpl implements OrganisationPolicyService {

    private final OrganisationPolicyValidator validator;
    private final OrganisationPolicyRepository organisationPolicyRepository;
    private final OrganisationRepository organisationRepository;

    @Override
    public OrganisationPolicy createOrUpdatePolicy(Long orgId, OrganisationPolicyDTO policyDTO) {
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation not found"));

        OrganisationPolicy policy = organisationPolicyRepository.findByOrganisation(org)
                .orElse(new OrganisationPolicy());

        validator.validate(policyDTO);
        // 🔹 Use mapper helper
        mapDtoToEntity(policyDTO, policy);
        policy.setOrganisation(org);

        if (policy.getId() == null) {
            policy.setCreatedAt(LocalDateTime.now());
        }
        policy.setUpdatedAt(LocalDateTime.now());

        return organisationPolicyRepository.save(policy);
    }

    @Override
    public void createDefaultPolicy(Organisation org) {

        if (organisationPolicyRepository.findByOrganisation(org).isPresent()) {
            return;
        }

        OrganisationPolicy policy = OrganisationPolicy.builder()
                .organisation(org)
                .officeStart(LocalTime.of(10, 0))
                .officeClosed(LocalTime.of(19, 0))
                .salaryCycleStartDay(1)
                .cycleDuration(30)
                .payslipGenerationOffsetDays(5)
                .standardDailyHours(8.0)
                .weeklyHours(40.0)
                .monthlyHours(160.0)
                .autoPunchOutTime(LocalTime.of(20, 0))
                .maxWorkHoursBeforeAutoPunchOut(10)
                .lateGraceMinutes(10)
                .earlyOutGraceMinutes(10)
                .weekOffDays(new ArrayList<>(List.of(DayOfWeek.SUNDAY)))
                .carryForwardEnabled(false)
                .encashmentEnabled(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        organisationPolicyRepository.save(policy);
    }

    @Override
    public Optional<OrganisationPolicy> getPolicyForOrg(Long orgId) {
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation not found"));
        return organisationPolicyRepository.findByOrganisation(org);
    }

    @Override
    public OrganisationPolicyDTO getOrganisationPolicyByOrgId(Long orgId) {
        OrganisationPolicy policy = getPolicyForOrg(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation policy not found"));
        return mapEntityToDto(policy);
    }

    @Override
    public int getAutoPunchOutAfterHours(Long orgId) {
        OrganisationPolicy policy = getPolicyForOrg(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation policy not found"));
        return policy.getAutoPunchOutTime() != null ? policy.getAutoPunchOutTime().getHour() : 0;
    }

    @Override
    public boolean isLeaveEncashmentEnabled(Long orgId) {
        OrganisationPolicy policy = getPolicyForOrg(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation policy not found"));
        return Boolean.TRUE.equals(policy.getEncashmentEnabled());
    }

    /*
     * -------------------------------------------------------------------
     * 🔹 Private Mapper Utility Methods (Null-safe)
     * -------------------------------------------------------------------
     */

    private void mapDtoToEntity(OrganisationPolicyDTO dto, OrganisationPolicy entity) {
        if (dto == null || entity == null)
            return;

        if (dto.getFinancialYearStartDay() != null)
            entity.setFinancialYearStartDay(dto.getFinancialYearStartDay());
        if (dto.getFinancialYearStartMonth() != null)
            entity.setFinancialYearStartMonth(dto.getFinancialYearStartMonth());
        if (dto.getLeaveYearStartDay() != null)
            entity.setLeaveYearStartDay(dto.getLeaveYearStartDay());
        if (dto.getLeaveYearStartMonth() != null)
            entity.setLeaveYearStartMonth(dto.getLeaveYearStartMonth());
        if (dto.getStandardDailyHours() != null)
            entity.setStandardDailyHours(dto.getStandardDailyHours());
        if (dto.getOfficeStart() != null)
            entity.setOfficeStart(dto.getOfficeStart());
        if (dto.getOfficeClosed() != null)
            entity.setOfficeClosed(dto.getOfficeClosed());
        if (dto.getWeeklyHours() != null)
            entity.setWeeklyHours(dto.getWeeklyHours());
        ;
        if (dto.getLateGraceMinutes() != null)
            entity.setLateGraceMinutes(dto.getLateGraceMinutes());
        if (dto.getEarlyOutGraceMinutes() != null)
            entity.setEarlyOutGraceMinutes(dto.getEarlyOutGraceMinutes());
        if (dto.getOvertimeAllowed() != null)
            entity.setOvertimeAllowed(dto.getOvertimeAllowed());
        if (dto.getOvertimeMultiplier() != null)
            entity.setOvertimeMultiplier(dto.getOvertimeMultiplier());
        if (dto.getMinMonthlyHours() != null)
            entity.setMinMonthlyHours(dto.getMinMonthlyHours());
        if (dto.getCarryForwardEnabled() != null)
            entity.setCarryForwardEnabled(dto.getCarryForwardEnabled());
        if (dto.getEncashmentEnabled() != null)
            entity.setEncashmentEnabled(dto.getEncashmentEnabled());
        if (dto.getAdditionalNotes() != null)
            entity.setAdditionalNotes(dto.getAdditionalNotes());

        if (dto.getMonthlyHours() != null)
            entity.setMonthlyHours(dto.getMonthlyHours());

        if (dto.getAutoPunchOutTime() != null)
            entity.setAutoPunchOutTime(dto.getAutoPunchOutTime());

        if (dto.getMaxWorkHoursBeforeAutoPunchOut() != null)
            entity.setMaxWorkHoursBeforeAutoPunchOut(dto.getMaxWorkHoursBeforeAutoPunchOut());

        if (dto.getWeekOffDays() != null && !dto.getWeekOffDays().isEmpty())
            entity.setWeekOffDays(dto.getWeekOffDays());

        if (dto.getSalaryCycleStartDay() != null)
            entity.setSalaryCycleStartDay(dto.getSalaryCycleStartDay());

        // if (dto.getCycleDuration() != null)
        // entity.setCycleDuration(dto.getCycleDuration());

        if (dto.getPayslipGenerationOffsetDays() != null)
            entity.setPayslipGenerationOffsetDays(dto.getPayslipGenerationOffsetDays());
    }

    private OrganisationPolicyDTO mapEntityToDto(OrganisationPolicy policy) {
        if (policy == null)
            return null;

        OrganisationPolicyDTO dto = new OrganisationPolicyDTO();
        dto.setId(policy.getId());
        dto.setOrganisationId(policy.getOrganisation() != null ? policy.getOrganisation().getId() : null);
        dto.setFinancialYearStartDay(policy.getFinancialYearStartDay());
        dto.setFinancialYearStartMonth(policy.getFinancialYearStartMonth());
        dto.setLeaveYearStartDay(policy.getLeaveYearStartDay());
        dto.setLeaveYearStartMonth(policy.getLeaveYearStartMonth());
        dto.setStandardDailyHours(policy.getStandardDailyHours());
        dto.setOfficeClosed(policy.getOfficeClosed());
        dto.setOfficeStart(policy.getOfficeStart());
        dto.setWeeklyHours(policy.getWeeklyHours());
        dto.setMonthlyHours(policy.getMonthlyHours());
        dto.setAutoPunchOutTime(policy.getAutoPunchOutTime());
        dto.setMaxWorkHoursBeforeAutoPunchOut(policy.getMaxWorkHoursBeforeAutoPunchOut());
        dto.setWeekOffDays(policy.getWeekOffDays());
        dto.setSalaryCycleStartDay(policy.getSalaryCycleStartDay());
        // dto.setCycleDuration(policy.getCycleDuration());
        dto.setPayslipGenerationOffsetDays(policy.getPayslipGenerationOffsetDays());
        dto.setLateGraceMinutes(policy.getLateGraceMinutes());
        dto.setEarlyOutGraceMinutes(policy.getEarlyOutGraceMinutes());
        dto.setOvertimeAllowed(policy.getOvertimeAllowed());
        dto.setOvertimeMultiplier(policy.getOvertimeMultiplier());
        dto.setMinMonthlyHours(policy.getMinMonthlyHours());
        dto.setCarryForwardEnabled(policy.getCarryForwardEnabled());
        dto.setEncashmentEnabled(policy.getEncashmentEnabled());
        dto.setAdditionalNotes(policy.getAdditionalNotes());
        dto.setCreatedAt(policy.getCreatedAt());
        dto.setUpdatedAt(policy.getUpdatedAt());

        return dto;
    }
}
