package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.dto.OrgAdminDTO;

import com.sellspark.SellsHRMS.entity.OrganisationAdmin;

import com.sellspark.SellsHRMS.repository.EmployeeRepository;
import com.sellspark.SellsHRMS.repository.OrganisationAdminRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.service.OrganisationAdminService;
import com.sellspark.SellsHRMS.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganisationAdminServiceImpl implements OrganisationAdminService {

    private final OrganisationAdminRepository orgAdminRepo;
    private final OrganisationRepository orgRepo;
    private final UserService userService;
    private final EmployeeRepository employeeRepo;

    @Override
    public OrganisationAdmin findByEmail(String email) {
        return orgAdminRepo.findByEmail(email).orElse(null);
    }

    // @Override
    // public OrganisationAdmin create(String fullName, String email, String
    // rawPassword, Long OrgId) {
    // OrganisationAdmin orgAdmin = new OrganisationAdmin();
    // Optional<Organisation> org = orgRepo.findById(OrgId);
    // orgAdmin.setEmail(email);
    // orgAdmin.setFullName(fullName);
    // orgAdmin.setOrganisation(org.get());
    // return orgAdminRepo.save(orgAdmin);
    // }

    @Override
    @Transactional
    public OrganisationAdmin create(OrgAdminDTO dto) {

        userService.createUser(
                dto.getEmail(),
                dto.getPassword(),
                "ORG_ADMIN",
                dto.getOrganisationId());

        OrganisationAdmin admin = new OrganisationAdmin();
        admin.setFullName(dto.getFullName());
        admin.setEmail(dto.getEmail());
        admin.setOrganisation(
                orgRepo.findById(dto.getOrganisationId())
                        .orElseThrow(() -> new RuntimeException("Organisation not found: " + dto.getOrganisationId())));

        return orgAdminRepo.save(admin);
    }

    @Override
    public OrganisationAdmin getById(Long id) {
        return orgAdminRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("OrgAdmin not found"));
    }

    @Override
    public List<OrganisationAdmin> getByOrganisationId(Long orgId) {
        return orgAdminRepo.findByOrganisation(orgRepo.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organisation does not exits by orgId: " + orgId)));
    }

    @Override
    public List<OrganisationAdmin> getAll() {
        return orgAdminRepo.findAll();
    }

    @Override
    public OrganisationAdmin update(Long id, OrgAdminDTO dto) {
        return orgAdminRepo.findById(id)
                .map(admin -> {
                    admin.setFullName(dto.getFullName());
                    admin.setEmail(dto.getEmail());
                    return orgAdminRepo.save(admin);
                })
                .orElseThrow(() -> new RuntimeException("OrgAdmin not found"));
    }

    @Override
    public void delete(Long id) {
        orgAdminRepo.deleteById(id);
    }

    @Override
    public int getEmployeeCount(Long organisationId) {
        return employeeRepo.countByOrganisationId(organisationId);
    }

}
