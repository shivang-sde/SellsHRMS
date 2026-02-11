package com.sellspark.SellsHRMS.controller.api.asset;

import com.sellspark.SellsHRMS.dto.asset.AssetCategoryDTO;
import com.sellspark.SellsHRMS.service.asset.AssetCategoryService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asset-categories")
@RequiredArgsConstructor
public class AssetCategoryRestController {

    private final AssetCategoryService service;

    @PostMapping
    public ResponseEntity<AssetCategoryDTO> create(@RequestBody AssetCategoryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetCategoryDTO> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/org/{orgId}")
    public ResponseEntity<List<AssetCategoryDTO>> getAllByOrg(@PathVariable Long orgId) {
        return ResponseEntity.ok(service.getAllByOrgId(orgId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AssetCategoryDTO> update(@PathVariable Long id, @RequestBody AssetCategoryDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
