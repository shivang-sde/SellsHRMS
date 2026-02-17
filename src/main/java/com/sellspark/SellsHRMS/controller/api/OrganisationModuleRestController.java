package com.sellspark.SellsHRMS.controller.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sellspark.SellsHRMS.dto.common.ApiResponse;
import com.sellspark.SellsHRMS.entity.Module;
import com.sellspark.SellsHRMS.service.OrganisationModuleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
public class OrganisationModuleRestController {

    private final OrganisationModuleService service;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Module>>> getAllModules() {
        List<Module> modules = service.getAllModules();
        return ResponseEntity.ok(ApiResponse.ok("Modules fetched successfully", modules));
    }

    @GetMapping("/codes/org/{orgId}/active")
    public ResponseEntity<ApiResponse<List<String>>> getActiveModuleCodes(
            @PathVariable Long orgId) {
        List<String> activeCodes = service.getActiveModuleCodes(orgId);
        return ResponseEntity.ok(ApiResponse.ok("Active modules fetched successfully", activeCodes));
    }

    @GetMapping("/org/{orgId}/active-modules")
    public ResponseEntity<ApiResponse<List<String>>> getActiveModules(
            @PathVariable Long orgId) {
        List<String> activeModules = service.findActiveModuleCodesByOrganisationId(orgId);
        return ResponseEntity.ok(ApiResponse.ok("Active modules fetched successfully", activeModules));
    }

    @PostMapping("/org/{orgId}/assign")
    public ResponseEntity<ApiResponse<Void>> assignModules(
            @PathVariable Long orgId,
            @RequestBody List<String> moduleCodes) {

        service.assignModuleToOrganisation(orgId, moduleCodes);

        return ResponseEntity.ok(ApiResponse.ok("Modules assigned successfully"));
    }

}
