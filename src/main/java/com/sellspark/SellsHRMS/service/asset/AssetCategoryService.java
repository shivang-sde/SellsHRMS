package com.sellspark.SellsHRMS.service.asset;

import com.sellspark.SellsHRMS.dto.asset.AssetCategoryDTO;
import java.util.List;

public interface AssetCategoryService {
    AssetCategoryDTO create(AssetCategoryDTO dto);

    AssetCategoryDTO getById(Long id);

    List<AssetCategoryDTO> getAllByOrgId(Long orgId);

    AssetCategoryDTO update(Long id, AssetCategoryDTO dto);

    void delete(Long id);
}
