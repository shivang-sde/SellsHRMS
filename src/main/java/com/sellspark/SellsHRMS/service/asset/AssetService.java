package com.sellspark.SellsHRMS.service.asset;

import com.sellspark.SellsHRMS.dto.asset.AssetDTO;
import java.util.List;

public interface AssetService {
    AssetDTO create(AssetDTO dto);

    AssetDTO getById(Long id);

    List<AssetDTO> getAllByOrgId(Long orgId);

    AssetDTO update(Long id, AssetDTO dto);

    void delete(Long id);

    AssetDTO assignToEmployee(Long assetId, Long employeeId, String remarks);

    AssetDTO returnAsset(Long assetId);
}
