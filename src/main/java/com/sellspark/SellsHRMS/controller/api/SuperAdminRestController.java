package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.dto.OrgAdminDTO;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.OrganisationAdmin;
import com.sellspark.SellsHRMS.service.OrganisationAdminService;
import com.sellspark.SellsHRMS.service.OrganisationService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/superadmin")
@RequiredArgsConstructor
public class SuperAdminRestController {

    private final OrganisationService organisationService;
    private final OrganisationAdminService organisationAdminService;

    // --- ORGANISATION CRUD ---

    @PostMapping("/organisation")
    public Organisation createOrganisation(@RequestBody Organisation org) {
        return organisationService.create(org);
    }

    @PutMapping("/organisation/{id}")
    public Organisation updateOrganisation(@PathVariable Long id, @RequestBody Organisation org) {
        return organisationService.update(id, org);
    }

    @GetMapping("/organisations")
    public List<Organisation> getAllOrganisations() {
        return organisationService.getAll();
    }

    // --- ORG ADMIN CREATION/LIST ---

    @PostMapping("/organisation-admin")
    public OrganisationAdmin createOrganisationAdmin(@RequestBody OrgAdminDTO dto) {
        return organisationAdminService.create(dto);
    }

    @GetMapping("/org-admins")
    public List<OrganisationAdmin> getAllOrgAdmins() {
        return organisationAdminService.getAll();
    }
}
