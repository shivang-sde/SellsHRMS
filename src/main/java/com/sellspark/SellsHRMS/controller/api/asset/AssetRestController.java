package com.sellspark.SellsHRMS.controller.api.asset;

import com.sellspark.SellsHRMS.dto.asset.AssetDTO;
import com.sellspark.SellsHRMS.service.asset.AssetService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetRestController {

    private final AssetService service;

    @PostMapping
    public ResponseEntity<AssetDTO> create(@RequestBody AssetDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetDTO> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/org/{orgId}")
    public ResponseEntity<List<AssetDTO>> getAllByOrg(@PathVariable Long orgId) {
        return ResponseEntity.ok(service.getAllByOrgId(orgId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AssetDTO> update(@PathVariable Long id, @RequestBody AssetDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{assetId}/assign")
    public ResponseEntity<AssetDTO> assign(@PathVariable Long assetId, @RequestBody Map<String, Object> body) {
        Long employeeId = Long.valueOf(body.get("employeeId").toString());
        String remarks = body.get("remarks") != null ? body.get("remarks").toString() : null;
        return ResponseEntity.ok(service.assignToEmployee(assetId, employeeId, remarks));
    }

    @PostMapping("/{assetId}/return")
    public ResponseEntity<AssetDTO> returnAsset(@PathVariable Long assetId) {
        return ResponseEntity.ok(service.returnAsset(assetId));
    }
}
