package com.sellspark.SellsHRMS.service.superadmin;

import com.sellspark.SellsHRMS.dto.OrgAdminDTO;
import com.sellspark.SellsHRMS.dto.OrganisationRequest;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.OrganisationAdmin;
import com.sellspark.SellsHRMS.repository.OrganisationAdminRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final OrganisationRepository organisationRepo;
    private final OrganisationAdminRepository adminRepo;
    private final PasswordEncoder passwordEncoder;

    public Organisation createOrganisation(OrganisationRequest request) {

        organisationRepo.findByName(request.getName()).ifPresent(o -> {
            throw new RuntimeException("Organisation name already exists");
        });

        Organisation organisation = Organisation.builder()
                .name(request.getName())
                .domain(request.getDomain())
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .maxEmployees(request.getMaxEmployees())
                .subscriptionStatus(Organisation.SubscriptionStatus.ACTIVE)
                .isActive(true)
                .build();

        return organisationRepo.save(organisation);
    }

    public OrganisationAdmin createOrganisationAdmin(OrgAdminDTO request) {

        Organisation organisation = organisationRepo.findById(request.getOrganisationId())
                .orElseThrow(() -> new RuntimeException("Organisation not found"));

        adminRepo.findByEmail(request.getEmail()).ifPresent(a -> {
            throw new RuntimeException("Email already registered as admin");
        });

        OrganisationAdmin admin = OrganisationAdmin.builder()
                .organisation(organisation)
                .fullName(request.getFullName())
                .email(request.getEmail())
                .isActive(true)
                .build();

        return adminRepo.save(admin);
    }

    public Organisation updateOrganisationStatus(Long orgId, boolean active) {
        Organisation org = organisationRepo.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organisation not found"));
        org.setIsActive(active);
        return organisationRepo.save(org);
    }

    public OrganisationAdmin updateOrgAdminStatus(Long adminId, boolean active) {
        OrganisationAdmin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        admin.setIsActive(active);
        return adminRepo.save(admin);

    }

    public List<Organisation> getAllOrganisations() {
        return organisationRepo.findAll();
    }

    public List<OrganisationAdmin> getAllOrganisationAdmins() {
        return adminRepo.findAll();
    }
}
