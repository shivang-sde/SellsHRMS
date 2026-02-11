package com.sellspark.SellsHRMS.controller.api.asset;

import com.sellspark.SellsHRMS.dto.asset.AssetMaintenanceLogDTO;
import com.sellspark.SellsHRMS.service.asset.AssetMaintenanceLogService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asset-maintenance")
@RequiredArgsConstructor
public class AssetMaintenanceLogRestController {

    private final AssetMaintenanceLogService service;

    @PostMapping
    public ResponseEntity<AssetMaintenanceLogDTO> create(@RequestBody AssetMaintenanceLogDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetMaintenanceLogDTO> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/org/{orgId}")
    public ResponseEntity<List<AssetMaintenanceLogDTO>> getAllByOrg(@PathVariable Long orgId) {
        return ResponseEntity.ok(service.getAllByOrgId(orgId));
    }

    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<AssetMaintenanceLogDTO>> getByAsset(@PathVariable Long assetId) {
        return ResponseEntity.ok(service.getByAssetId(assetId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
