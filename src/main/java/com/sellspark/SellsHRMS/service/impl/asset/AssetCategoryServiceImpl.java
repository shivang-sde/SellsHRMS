package com.sellspark.SellsHRMS.service.impl.asset;

import com.sellspark.SellsHRMS.dto.asset.AssetCategoryDTO;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.asset.AssetCategory;
import com.sellspark.SellsHRMS.mapper.asset.AssetCategoryMapper;
import com.sellspark.SellsHRMS.repository.asset.AssetCategoryRepository;
import com.sellspark.SellsHRMS.service.asset.AssetCategoryService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AssetCategoryServiceImpl implements AssetCategoryService {

    private final AssetCategoryRepository repo;
    private final AssetCategoryMapper mapper;

    @Override
    public AssetCategoryDTO create(AssetCategoryDTO dto) {
        AssetCategory entity = new AssetCategory();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        Organisation org = new Organisation();
        org.setId(dto.getOrgId());
        entity.setOrganisation(org);

        return mapper.toDTO(repo.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public AssetCategoryDTO getById(Long id) {
        return mapper.toDTO(repo.findById(id)
                .orElseThrow(() -> new RuntimeException("AssetCategory not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetCategoryDTO> getAllByOrgId(Long orgId) {
        return repo.findByOrganisation_Id(orgId).stream()
                .map(mapper::toDTO).toList();
    }

    @Override
    public AssetCategoryDTO update(Long id, AssetCategoryDTO dto) {
        AssetCategory existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("AssetCategory not found"));
        if (dto.getName() != null)
            existing.setName(dto.getName());
        if (dto.getDescription() != null)
            existing.setDescription(dto.getDescription());
        if (dto.getIsActive() != null)
            existing.setIsActive(dto.getIsActive());
        return mapper.toDTO(repo.save(existing));
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
