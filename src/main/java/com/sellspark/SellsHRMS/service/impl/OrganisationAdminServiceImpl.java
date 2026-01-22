package com.sellspark.SellsHRMS.service.impl;


import com.sellspark.SellsHRMS.dto.admin.OrgAdminPatchRequest;
import com.sellspark.SellsHRMS.dto.admin.OrgAdminSummaryDTO;
import com.sellspark.SellsHRMS.dto.admin.OrgAdminUpdateDTO;
import com.sellspark.SellsHRMS.dto.mapper.DtoMapper;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.OrganisationAdmin;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.repository.EmployeeRepository;
import com.sellspark.SellsHRMS.repository.OrganisationAdminRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.service.OrganisationAdminService;


import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganisationAdminServiceImpl implements OrganisationAdminService {

    private final OrganisationAdminRepository orgAdminRepo;
    private final OrganisationRepository orgRepo;
    private final DtoMapper mapper;
    private final EmployeeRepository employeeRepo;

    @Override
    public OrganisationAdmin findByEmail(String email) {
        return orgAdminRepo.findByEmail(email).orElse(null);
    }


    @Override
    @Transactional(readOnly = true)
    public OrgAdminSummaryDTO getById(Long id) {
        OrganisationAdmin admin = orgAdminRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrganisationAdmin", "id", id));
        return mapper.toAdminSummary(admin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrgAdminSummaryDTO> getAll() {
        return orgAdminRepo.findAll().stream().map(mapper::toAdminSummary).toList();
    }

    @Override
public OrgAdminSummaryDTO patchUpdate(Long id, OrgAdminPatchRequest dto) {
    OrganisationAdmin admin = orgAdminRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("OrganisationAdmin", "id", id));
    if (dto.getFullName() != null) admin.setFullName(dto.getFullName());
    if (dto.getEmail() != null) admin.setEmail(dto.getEmail());
    if (dto.getIsActive() != null) admin.setIsActive(dto.getIsActive());
    orgAdminRepo.save(admin);
    return mapper.toAdminSummary(admin);
}

    @Override
    public OrgAdminSummaryDTO update(Long id, OrgAdminUpdateDTO dto) {
        OrganisationAdmin admin = orgAdminRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrganisationAdmin", "id", id));

        if (dto.getFullName() != null) admin.setFullName(dto.getFullName());
        if (dto.getEmail() != null) admin.setEmail(dto.getEmail());
        if (dto.getIsActive() != null) admin.setIsActive(dto.getIsActive());

        orgAdminRepo.save(admin);
        return mapper.toAdminSummary(admin);
    }


    @Override
    public void activateOrgAdmin(Long id){
        OrganisationAdmin orgAdmin = orgAdminRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("OrganisationAdmin", "id", id));
        orgAdmin.setIsActive(true);
        orgAdminRepo.save(orgAdmin);
    }

     @Override
    public void deactivateOrgAdmin(Long id){
        OrganisationAdmin orgAdmin = orgAdminRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("OrganisationAdmin", "id", id));
        orgAdmin.setIsActive(false);
        orgAdminRepo.save(orgAdmin);
    }


    @Override
    public void delete(Long id) {
        orgAdminRepo.deleteById(id);
    }

    @Override
public List<OrgAdminSummaryDTO> getByOrganisationId(Long orgId) {
    Organisation org = orgRepo.findById(orgId).orElseThrow(() -> new ResourceNotFoundException("Organisation", "id", orgId));
    return orgAdminRepo.findByOrganisation(org).stream().map(mapper::toAdminSummary).toList();
}

    @Override
    public int getEmployeeCount(Long organisationId) {
        return employeeRepo.countByOrganisationId(organisationId);
    }

}
