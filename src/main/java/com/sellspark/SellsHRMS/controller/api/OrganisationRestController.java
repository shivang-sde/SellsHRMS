package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.dto.admin.OrgAdminSummaryDTO;
import com.sellspark.SellsHRMS.dto.organisation.*;
import com.sellspark.SellsHRMS.service.OrganisationAdminService;
import com.sellspark.SellsHRMS.service.OrganisationPolicyService;
import com.sellspark.SellsHRMS.service.OrganisationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;




@RestController
@RequestMapping("/api/organisation")
@RequiredArgsConstructor
public class OrganisationRestController {

    private final OrganisationService organisationService;
    private final OrganisationPolicyService organisationPolicyService;
    private final OrganisationAdminService orgAdminService;

    // Create organisation (option A: may include admin inside create DTO)
    @PostMapping("/create")
    public ResponseEntity<OrganisationDTO> create(@RequestBody OrganisationDTO dto) {
        OrganisationDTO saved = organisationService.create(dto);
        return ResponseEntity.ok(saved);
    }


    // Get one
    @GetMapping("/{id}")
    public ResponseEntity<OrganisationDTO> get(@PathVariable Long id) {
        OrganisationDTO dto = organisationService.getById(id);
        return ResponseEntity.ok(dto);
    }

   // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        organisationService.delete(id);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/{orgId}/policy/create")
    public ResponseEntity<OrganisationPolicyDTO> createOrganisationPolicy(@PathVariable Long orgId, @RequestBody OrganisationPolicyDTO dto) {
        organisationPolicyService.createOrUpdatePolicy(orgId, dto);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{orgId}/policy")
    public ResponseEntity<OrganisationPolicyDTO> getOrganisationPolicy(@PathVariable Long orgId) {
        OrganisationPolicyDTO policy = organisationPolicyService.getOrganisationPolicyByOrgId(orgId);
        return ResponseEntity.ok(policy);
    }
    
    @PutMapping("/{orgId}/policy/{policyId}/update")
    public ResponseEntity<OrganisationPolicyDTO> updateOrganisationPolicy(@PathVariable Long orgId, @PathVariable Long policyId, @RequestBody OrganisationPolicyDTO dto) {
        organisationPolicyService.createOrUpdatePolicy(orgId, dto);
        return ResponseEntity.ok(dto);
    }
    

    // List all (summary)
    @GetMapping
    public ResponseEntity<List<OrganisationDTO>> list() {
        List<OrganisationDTO> list = organisationService.getAllOrganisations();
        return ResponseEntity.ok(list);
    }

    // List admins of organisation
    @GetMapping("/{id}/admins")
    public ResponseEntity<List<OrgAdminSummaryDTO>> getAdmins(@PathVariable Long id) {
        List<OrgAdminSummaryDTO> admins = orgAdminService.getByOrganisationId(id);
        return ResponseEntity.ok(admins);
    }


}
