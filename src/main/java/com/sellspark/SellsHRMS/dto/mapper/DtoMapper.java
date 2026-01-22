package com.sellspark.SellsHRMS.dto.mapper;

import com.sellspark.SellsHRMS.dto.admin.OrgAdminCreateDTO;
import com.sellspark.SellsHRMS.dto.admin.OrgAdminSummaryDTO;
import com.sellspark.SellsHRMS.dto.employee.*;
import com.sellspark.SellsHRMS.dto.organisation.*;
import com.sellspark.SellsHRMS.entity.*;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DtoMapper {

    // ==================== ORGANISATION =====================

    // Organisation create DTO -> entity (partial)
    public Organisation toOrganisationEntity(OrganisationDTO dto) {
        Organisation org = new Organisation();
        org.setName(dto.getName());
        org.setDomain(dto.getDomain());
        org.setLogoUrl(dto.getLogoUrl());
        org.setContactEmail(dto.getContactEmail());
        org.setContactPhone(dto.getContactPhone());
        org.setAddress(dto.getAddress());
        org.setCountry(dto.getCountry());
        org.setPan(dto.getPan());
        org.setTan(dto.getTan());
        org.setMaxEmployees(dto.getMaxEmployees());
        org.setValidity(dto.getValidity());
        org.setIsActive(true);

        return org;
    }

    // Organisation entity -> summary DTO
    public OrganisationSummaryDTO toOrganisationSummary(Organisation org) {
        OrganisationSummaryDTO dto = new OrganisationSummaryDTO();
        dto.setId(org.getId());
        dto.setName(org.getName());
        dto.setDomain(org.getDomain());
        dto.setMaxEmployees(org.getMaxEmployees());
        dto.setIsActive(org.getIsActive());
        dto.setValidity(org.getValidity());

        if (org.getOrgAdmin() != null) {
            dto.setAdminName(org.getOrgAdmin().getFullName());
            dto.setAdminEmail(org.getOrgAdmin().getEmail());
        }
        return dto;
    }

    // Organisation entity -> detail DTO
    public OrganisationDetailDTO toOrganisationDetail(Organisation org) {
        log.info("org get by id: {}", org);
        OrganisationDetailDTO dto = new OrganisationDetailDTO();
        dto.setId(org.getId());
        dto.setName(org.getName());
        dto.setDomain(org.getDomain());
        dto.setLogoUrl(org.getLogoUrl());
        dto.setIsActive(org.getIsActive());
        dto.setContactEmail(org.getContactEmail());
        dto.setContactPhone(org.getContactPhone());
        dto.setAddress(org.getAddress());
        dto.setCountry(org.getCountry());
        dto.setMaxEmployees(org.getMaxEmployees());

        if (org.getOrgAdmin() != null) {
            dto.setAdmin(toAdminSummary(org.getOrgAdmin()));
        }
        return dto;
    }

    // OrgAdmin create DTO -> entity (partial)
    public OrganisationAdmin toAdminEntity(OrganisationDTO dto) {
        OrganisationAdmin a = new OrganisationAdmin();
        a.setFullName(dto.getAdminFullName());
        a.setEmail(dto.getAdminEmail());
        a.setIsActive(true);
        return a;
    }

    // OrgAdmin entity -> summary DTO
    public OrgAdminSummaryDTO toAdminSummary(OrganisationAdmin admin) {
        OrgAdminSummaryDTO dto = new OrgAdminSummaryDTO();
        dto.setId(admin.getId());
        dto.setFullName(admin.getFullName());
        dto.setEmail(admin.getEmail());
        dto.setIsActive(admin.getIsActive());
        dto.setLastLogin(admin.getLastLogin());
        if (admin.getOrganisation() != null) {
            dto.setOrganisationId(admin.getOrganisation().getId());
            dto.setOrganisationName(admin.getOrganisation().getName());
        }
        return dto;
    }


    // ==================== EMPLOYEE =====================

    public EmployeeListDTO toEmployeeList(Employee e) {
        EmployeeListDTO dto = new EmployeeListDTO();

        dto.setId(e.getId());
        dto.setEmployeeCode(e.getEmployeeCode());
        dto.setFirstName(e.getFirstName());
        dto.setLastName(e.getLastName());
        dto.setEmail(e.getEmail());

        dto.setDepartment(e.getDepartment() != null ? e.getDepartment().getName() : null);
        dto.setDesignation(e.getDesignation() != null ? e.getDesignation().getTitle() : null);
        dto.setStatus(e.getStatus() != null ? e.getStatus().name() : null);

        return dto;
    }

   
}
