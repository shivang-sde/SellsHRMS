package com.sellspark.SellsHRMS.service.impl.asset;

import com.sellspark.SellsHRMS.dto.asset.AssetMaintenanceLogDTO;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.asset.Asset;
import com.sellspark.SellsHRMS.entity.asset.AssetMaintenanceLog;
import com.sellspark.SellsHRMS.mapper.asset.AssetMaintenanceLogMapper;
import com.sellspark.SellsHRMS.repository.asset.AssetMaintenanceLogRepository;
import com.sellspark.SellsHRMS.service.asset.AssetMaintenanceLogService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AssetMaintenanceLogServiceImpl implements AssetMaintenanceLogService {

    private final AssetMaintenanceLogRepository repo;
    private final AssetMaintenanceLogMapper mapper;

    @Override
    public AssetMaintenanceLogDTO create(AssetMaintenanceLogDTO dto) {
        AssetMaintenanceLog entity = new AssetMaintenanceLog();

        Asset asset = new Asset();
        asset.setId(dto.getAssetId());
        entity.setAsset(asset);

        entity.setMaintenanceDate(dto.getMaintenanceDate());
        entity.setDescription(dto.getDescription());
        entity.setCost(dto.getCost());
        entity.setPerformedBy(dto.getPerformedBy());

        Organisation org = new Organisation();
        org.setId(dto.getOrgId());
        entity.setOrganisation(org);

        return mapper.toDTO(repo.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public AssetMaintenanceLogDTO getById(Long id) {
        return mapper.toDTO(repo.findById(id)
                .orElseThrow(() -> new RuntimeException("MaintenanceLog not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetMaintenanceLogDTO> getAllByOrgId(Long orgId) {
        return repo.findByOrganisation_Id(orgId).stream()
                .map(mapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetMaintenanceLogDTO> getByAssetId(Long assetId) {
        return repo.findByAsset_Id(assetId).stream()
                .map(mapper::toDTO).toList();
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
