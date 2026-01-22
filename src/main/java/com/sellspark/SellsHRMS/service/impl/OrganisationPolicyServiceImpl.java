package com.sellspark.SellsHRMS.service.impl;

import java.util.Optional;

import org.apache.tomcat.util.openssl.pem_password_cb;
import org.springframework.stereotype.Service;

import com.sellspark.SellsHRMS.dto.organisation.OrganisationPolicyDTO;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.OrganisationPolicy;
import com.sellspark.SellsHRMS.repository.OrganisationPolicyRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.service.OrganisationPolicyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganisationPolicyServiceImpl implements OrganisationPolicyService {

    private final OrganisationPolicyRepository organisationPolicyRepository;
    private final OrganisationRepository organisationRepository;

    @Override
    public OrganisationPolicy createOrUpdatePolicy(Long orgId, OrganisationPolicyDTO policyDTO) {
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organisation not found"));

        OrganisationPolicy policy = organisationPolicyRepository.findByOrganisation(org)
                .orElse(new OrganisationPolicy());

        policy.setOrganisation(org);
        policy.setFinancialYearStartDay(policyDTO.getFinancialYearStartDay());
        policy.setFinancialYearStartMonth(policyDTO.getFinancialYearStartMonth());
        policy.setLeaveYearStartDay(policyDTO.getLeaveYearStartDay());
        policy.setLeaveYearStartMonth(policyDTO.getLeaveYearStartMonth());
        policy.setStandardDailyHours(policyDTO.getStandardDailyHours());
        policy.setWeeklyHours(policyDTO.getWeeklyHours());
        policy.setAutoPunchTime(policyDTO.getAutoPunchTime());
        policy.setLateGraceMinutes(policyDTO.getLateGraceMinutes());
        policy.setEarlyOutGraceMinutes(policyDTO.getEarlyOutGraceMinutes());
        policy.setOvertimeAllowed(policyDTO.getOvertimeAllowed());
        policy.setOvertimeMultiplier(policyDTO.getOvertimeMultiplier());
        policy.setMinMonthlyHours(policyDTO.getMinMonthlyHours());
        policy.setFlexibleHourModelEnabled(policyDTO.getFlexibleHourModelEnabled());
        policy.setCarryForwardEnabled(policyDTO.getCarryForwardEnabled());
        policy.setEncashmentEnabled(policyDTO.getEncashmentEnabled());
        policy.setAdditionalNotes(policyDTO.getAdditionalNotes());

        if(policy.getId() == null) {
            policy.setCreatedAt(java.time.LocalDateTime.now());
        }
        if(policy.getId() != null) {
            policy.setUpdatedAt(java.time.LocalDateTime.now());
        }

        return organisationPolicyRepository.save(policy);
       
    }

    @Override
    public Optional<OrganisationPolicy> getPolicyForOrg(Long orgId) {
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organisation not found"));

        return organisationPolicyRepository.findByOrganisation(org);
    }

    @Override
    public OrganisationPolicyDTO getOrganisationPolicyByOrgId(Long orgId) {
        OrganisationPolicy policy = getPolicyForOrg(orgId)
                .orElseThrow(() -> new RuntimeException("Organisation policy not found"));

        OrganisationPolicyDTO dto = new OrganisationPolicyDTO();

        dto.setId(policy.getId());
        dto.setOrganisationId(policy.getOrganisation().getId());
        dto.setFinancialYearStartDay(policy.getFinancialYearStartDay());
        dto.setFinancialYearStartMonth(policy.getFinancialYearStartMonth());
        dto.setLeaveYearStartDay(policy.getLeaveYearStartDay());
        dto.setLeaveYearStartMonth(policy.getLeaveYearStartMonth());
        dto.setStandardDailyHours(policy.getStandardDailyHours());
        dto.setWeeklyHours(policy.getWeeklyHours());
        dto.setAutoPunchTime(policy.getAutoPunchTime());
        dto.setLateGraceMinutes(policy.getLateGraceMinutes());
        dto.setEarlyOutGraceMinutes(policy.getEarlyOutGraceMinutes());
        dto.setOvertimeAllowed(policy.getOvertimeAllowed());
        dto.setOvertimeMultiplier(policy.getOvertimeMultiplier());
        dto.setMinMonthlyHours(policy.getMinMonthlyHours());
        dto.setFlexibleHourModelEnabled(policy.getFlexibleHourModelEnabled());
        dto.setCarryForwardEnabled(policy.getCarryForwardEnabled());
        dto.setEncashmentEnabled(policy.getEncashmentEnabled());
        dto.setAdditionalNotes(policy.getAdditionalNotes());
        dto.setUpdatedAt(policy.getUpdatedAt());
        return dto;
    }

    @Override
    public int getAutoPunchOutAfterHours(Long orgId) {
        OrganisationPolicy policy = getPolicyForOrg(orgId)
                .orElseThrow(() -> new RuntimeException("Organisation policy not found"));

        return policy.getAutoPunchTime() != null ? policy.getAutoPunchTime().getHour() : 0;
    }

     
   
    @Override
    public boolean isLeaveEncashmentEnabled(Long orgId) {
        OrganisationPolicy policy = getPolicyForOrg(orgId)
                .orElseThrow(() -> new RuntimeException("Organisation policy not found"));

        return policy.getEncashmentEnabled() != null ? policy.getEncashmentEnabled() : false;
    }

    
}