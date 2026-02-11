package com.sellspark.SellsHRMS.controller.api.asset;

import com.sellspark.SellsHRMS.dto.asset.AssetAssignmentDTO;
import com.sellspark.SellsHRMS.service.asset.AssetAssignmentService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asset-assignments")
@RequiredArgsConstructor
public class AssetAssignmentRestController {

    private final AssetAssignmentService service;

    @GetMapping("/org/{orgId}")
    public ResponseEntity<List<AssetAssignmentDTO>> getAllByOrg(@PathVariable Long orgId) {
        return ResponseEntity.ok(service.getAllByOrgId(orgId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetAssignmentDTO> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<AssetAssignmentDTO>> getByAsset(@PathVariable Long assetId) {
        return ResponseEntity.ok(service.getByAssetId(assetId));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AssetAssignmentDTO>> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(service.getByEmployeeId(employeeId));
    }
}
