package com.sellspark.SellsHRMS.service.impl.asset;

import com.sellspark.SellsHRMS.dto.asset.AssetAssignmentDTO;
import com.sellspark.SellsHRMS.mapper.asset.AssetAssignmentMapper;
import com.sellspark.SellsHRMS.repository.asset.AssetAssignmentRepository;
import com.sellspark.SellsHRMS.service.asset.AssetAssignmentService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AssetAssignmentServiceImpl implements AssetAssignmentService {

    private final AssetAssignmentRepository repo;
    private final AssetAssignmentMapper mapper;

    @Override
    public List<AssetAssignmentDTO> getAllByOrgId(Long orgId) {
        return repo.findByOrganisation_Id(orgId).stream()
                .map(mapper::toDTO).toList();
    }

    @Override
    public List<AssetAssignmentDTO> getByAssetId(Long assetId) {
        return repo.findByAsset_Id(assetId).stream()
                .map(mapper::toDTO).toList();
    }

    @Override
    public List<AssetAssignmentDTO> getByEmployeeId(Long employeeId) {
        return repo.findByEmployee_Id(employeeId).stream()
                .map(mapper::toDTO).toList();
    }

    @Override
    public AssetAssignmentDTO getById(Long id) {
        return mapper.toDTO(repo.findById(id)
                .orElseThrow(() -> new RuntimeException("AssetAssignment not found")));
    }
}
