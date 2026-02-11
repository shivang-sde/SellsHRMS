package com.sellspark.SellsHRMS.controller.api.asset;

import com.sellspark.SellsHRMS.dto.asset.VendorDTO;
import com.sellspark.SellsHRMS.service.asset.VendorService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
public class VendorRestController {

    private final VendorService service;

    @PostMapping
    public ResponseEntity<VendorDTO> create(@RequestBody VendorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorDTO> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/org/{orgId}")
    public ResponseEntity<List<VendorDTO>> getAllByOrg(@PathVariable Long orgId) {
        return ResponseEntity.ok(service.getAllByOrgId(orgId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VendorDTO> update(@PathVariable Long id, @RequestBody VendorDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
