package com.sellspark.SellsHRMS.service.impl.asset;

import com.sellspark.SellsHRMS.dto.asset.AssetDTO;
import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.asset.*;
import com.sellspark.SellsHRMS.entity.asset.Asset.AssetStatus;
import com.sellspark.SellsHRMS.mapper.asset.AssetMapper;
import com.sellspark.SellsHRMS.repository.EmployeeRepository;
import com.sellspark.SellsHRMS.repository.asset.AssetAssignmentRepository;
import com.sellspark.SellsHRMS.repository.asset.AssetRepository;
import com.sellspark.SellsHRMS.service.asset.AssetService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepo;
    private final AssetAssignmentRepository assignmentRepo;
    private final EmployeeRepository employeeRepo;
    private final AssetMapper mapper;

    @Override
    public AssetDTO create(AssetDTO dto) {
        // Unique code check
        if (assetRepo.existsByAssetCodeAndOrganisation_Id(dto.getAssetCode(), dto.getOrgId())) {
            throw new RuntimeException("Asset code '" + dto.getAssetCode() + "' already exists in this organisation");
        }

        Asset entity = new Asset();
        entity.setAssetCode(dto.getAssetCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPurchaseDate(dto.getPurchaseDate());
        entity.setCost(dto.getCost());

        if (dto.getCondition() != null)
            entity.setCondition(Asset.AssetCondition.valueOf(dto.getCondition()));
        if (dto.getStatus() != null)
            entity.setStatus(AssetStatus.valueOf(dto.getStatus()));

        // Category (optional)
        if (dto.getCategoryId() != null) {
            AssetCategory cat = new AssetCategory();
            cat.setId(dto.getCategoryId());
            entity.setCategory(cat);
        }
        // Vendor (optional)
        if (dto.getVendorId() != null) {
            Vendor v = new Vendor();
            v.setId(dto.getVendorId());
            entity.setVendor(v);
        }

        Organisation org = new Organisation();
        org.setId(dto.getOrgId());
        entity.setOrganisation(org);

        return mapper.toDTO(assetRepo.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public AssetDTO getById(Long id) {
        return mapper.toDTO(assetRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetDTO> getAllByOrgId(Long orgId) {
        return assetRepo.findByOrganisation_Id(orgId).stream()
                .map(mapper::toDTO).toList();
    }

    @Override
    public AssetDTO update(Long id, AssetDTO dto) {
        Asset existing = assetRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        if (dto.getName() != null)
            existing.setName(dto.getName());
        if (dto.getDescription() != null)
            existing.setDescription(dto.getDescription());
        if (dto.getPurchaseDate() != null)
            existing.setPurchaseDate(dto.getPurchaseDate());
        if (dto.getCost() != null)
            existing.setCost(dto.getCost());
        if (dto.getCondition() != null)
            existing.setCondition(Asset.AssetCondition.valueOf(dto.getCondition()));
        if (dto.getStatus() != null)
            existing.setStatus(AssetStatus.valueOf(dto.getStatus()));

        if (dto.getCategoryId() != null) {
            AssetCategory cat = new AssetCategory();
            cat.setId(dto.getCategoryId());
            existing.setCategory(cat);
        }
        if (dto.getVendorId() != null) {
            Vendor v = new Vendor();
            v.setId(dto.getVendorId());
            existing.setVendor(v);
        }

        return mapper.toDTO(assetRepo.save(existing));
    }

    @Override
    public void delete(Long id) {
        assetRepo.deleteById(id);
    }

    @Override
    @Transactional
    public AssetDTO assignToEmployee(Long assetId, Long employeeId, String remarks) {
        Asset asset = assetRepo.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        if (asset.getStatus() == AssetStatus.ASSIGNED) {
            throw new RuntimeException("Asset is already assigned");
        }

        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Update asset
        asset.setStatus(AssetStatus.ASSIGNED);
        asset.setAssignedTo(employee);
        assetRepo.save(asset);

        // Create assignment record
        AssetAssignment assignment = AssetAssignment.builder()
                .asset(asset)
                .employee(employee)
                .assignedDate(LocalDate.now())
                .remarks(remarks)
                .activeFlag(true)
                .organisation(asset.getOrganisation())
                .build();
        assignmentRepo.save(assignment);

        return mapper.toDTO(asset);
    }

    @Override
    @Transactional
    public AssetDTO returnAsset(Long assetId) {
        Asset asset = assetRepo.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        if (asset.getStatus() != AssetStatus.ASSIGNED) {
            throw new RuntimeException("Asset is not currently assigned");
        }

        // Deactivate assignment
        assignmentRepo.findByAsset_IdAndActiveFlagTrue(assetId).ifPresent(assignment -> {
            assignment.setActiveFlag(false);
            assignment.setReturnDate(LocalDate.now());
            assignmentRepo.save(assignment);
        });

        // Update asset
        asset.setStatus(AssetStatus.AVAILABLE);
        asset.setAssignedTo(null);
        assetRepo.save(asset);

        return mapper.toDTO(asset);
    }
}
