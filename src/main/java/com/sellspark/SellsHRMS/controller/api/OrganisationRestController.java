package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.OrganisationAdmin;
import com.sellspark.SellsHRMS.service.OrganisationAdminService;
import com.sellspark.SellsHRMS.service.OrganisationService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/organisations")
@RequiredArgsConstructor
public class OrganisationRestController {

    private final OrganisationService organisationService;
    private final OrganisationAdminService orgAdminService;

    // --------------------------------------
    // CREATE ORGANISATION
    // --------------------------------------
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Organisation organisation) {
        Organisation saved = organisationService.create(organisation);
        return ResponseEntity.ok(saved);
    }

    // --------------------------------------
    // UPDATE
    // --------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody Organisation organisation) {

        Organisation updated = organisationService.update(id, organisation);
        return ResponseEntity.ok(updated);
    }

    // --------------------------------------
    // GET ONE
    // --------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {

        Optional<Organisation> orgOpt = organisationService.getById(id);

        if (orgOpt.isPresent()) {
            return ResponseEntity.ok(orgOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    // --------------------------------------
    // DELETE
    // --------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        organisationService.delete(id);
        return ResponseEntity.ok().build();
    }

    // --------------------------------------
    // LIST ALL
    // --------------------------------------
    @GetMapping
    public ResponseEntity<?> list() {
        List<Organisation> list = organisationService.getAll();
        return ResponseEntity.ok(list);
    }

    // --------------------------------------
    // OPTIONAL: LIST ADMINS OF ORGANISATION
    // --------------------------------------
    @GetMapping("/{id}/admins")
    public ResponseEntity<?> getAdmins(@PathVariable Long id) {

        List<OrganisationAdmin> admins = orgAdminService.getByOrganisationId(id);
        return ResponseEntity.ok(admins);
    }

}
