package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.dto.admin.OrgAdminCreateDTO;
import com.sellspark.SellsHRMS.dto.admin.OrgAdminSummaryDTO;
import com.sellspark.SellsHRMS.dto.organisation.*;
import com.sellspark.SellsHRMS.service.OrganisationAdminService;
import com.sellspark.SellsHRMS.service.OrganisationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;


 @RestController
@RequestMapping("/api/superadmin")
@RequiredArgsConstructor
@Slf4j
public class SuperAdminRestController {

    private final OrganisationService organisationService;
    private final OrganisationAdminService organisationAdminService;

    // Create organisation + admin in one go
    @PostMapping("/organisation")
    public  ResponseEntity<OrganisationDTO> createOrganisation(@RequestBody OrganisationDTO dto) {
        log.info("org dto {}, {}, {}, {}", dto, dto.getAdminEmail(), dto.getAdminFullName(), dto.getAdminPassword());
        OrganisationDTO org = organisationService.create(dto);
        return ResponseEntity.ok(org);
    }

    /** Get All Organisations */
    @GetMapping("/organisations")
     public ResponseEntity<List<OrganisationDTO>> listOrganisations() {
        log.info("loading organisations...");
        List<OrganisationDTO> list = organisationService.getAllOrganisationsWithAdmins();
        log.info("size of org list {}", list.size());
        return ResponseEntity.ok(list);
    }

    

    // Get org detail
    @GetMapping("/organisation/{id}")
    public ResponseEntity<OrganisationDTO> getOrganisation(@PathVariable Long id) {
        OrganisationDTO org = organisationService.getById(id);
        return org != null ? ResponseEntity.ok(org) : ResponseEntity.notFound().build();
    }

    @PutMapping("/organisation/{id}")
    public ResponseEntity<OrganisationDTO> update(@PathVariable Long id, @RequestBody OrganisationDTO dto) {
        return ResponseEntity.ok(organisationService.updateOrganisation(id, dto));
    }

   /** Activate Organisation */
    @PutMapping("/organisation/{id}/activate")
    public ResponseEntity<String> activateOrganisation(@PathVariable Long id) {
        organisationService.updateStatus(id, true, null);
        return ResponseEntity.ok("Organisation activated successfully.");
    }

    /** Deactivate Organisation */
    @PutMapping("/organisation/{id}/deactivate")
    public ResponseEntity<String> deactivateOrganisation(
            @PathVariable Long id,
            @RequestBody(required = false) String reason
    ) {
        organisationService.updateStatus(id, false, reason);
        return ResponseEntity.ok("Organisation deactivated successfully.");
    }

    /** Extend organisation license validity */
    @PutMapping("/organisation/{id}/extend-validity")
    public ResponseEntity<OrganisationDTO> extendValidity(
            @PathVariable Long id,
            @RequestParam("date") String newDate
    ) {
        OrganisationDTO updated = organisationService.extendValidity(id, LocalDate.parse(newDate));
        return ResponseEntity.ok(updated);
    }

     /** Increase organisation max employees */
    @PutMapping("/organisation/{id}/increase-max")
    public ResponseEntity<OrganisationDTO> increaseMaxEmployees(
            @PathVariable Long id,
            @RequestParam("limit") Integer limit
    ) {
        OrganisationDTO updated = organisationService.increaseMaxEmployees(id, limit);
        return ResponseEntity.ok(updated);
    }


    // Delete
     /** Delete organisation */
    @DeleteMapping("/organisation/{id}")
    public ResponseEntity<Void> deleteOrganisation(@PathVariable Long id) {
        organisationService.delete(id);
        return ResponseEntity.noContent().build();
    }


    // -----------------------------------------------------------------------
    // ORG ADMIN MANAGEMENT
    // -----------------------------------------------------------------------

 /** Create Org Admin under organisation */
    // @PostMapping("/org-admin")
    // public ResponseEntity<OrgAdminSummaryDTO> createOrgAdmin(
    //         @RequestBody OrgAdminCreateDTO dto,
    //         @RequestParam Long organisationId
    // ) {
    //     OrgAdminSummaryDTO created = organisationAdminService.create(dto, organisationId);
    //     return ResponseEntity.created(URI.create("/api/superadmin/orgadmins/" + created.getId()))
    //                          .body(created);
    // }

    @PutMapping("/org-admin/{id}/activate")
    public ResponseEntity<String> activateOrgAdmin(
            @PathVariable Long id,
            @RequestBody(required = false) String reason
    ){
        organisationAdminService.activateOrgAdmin(id);
        return ResponseEntity.ok("Org admin Activated");
    }


        @PutMapping("/org-admin/{id}/deactivate")
    public ResponseEntity<String> deactivateOrgAdmin(
            @PathVariable Long id,
            @RequestBody(required = false) String reason
    ){
        organisationAdminService.deactivateOrgAdmin(id);
        return ResponseEntity.ok("Org admin Deactivated");
    }

    /** List all Org Admins */
    @GetMapping("/orgadmins")
    public ResponseEntity<List<OrgAdminSummaryDTO>> listOrgAdmins() {
        List<OrgAdminSummaryDTO> list = organisationAdminService.getAll();
        return ResponseEntity.ok(list);
    }
}
