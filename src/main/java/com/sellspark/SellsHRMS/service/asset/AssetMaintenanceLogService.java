package com.sellspark.SellsHRMS.service.asset;

import com.sellspark.SellsHRMS.dto.asset.AssetMaintenanceLogDTO;
import java.util.List;

public interface AssetMaintenanceLogService {
    AssetMaintenanceLogDTO create(AssetMaintenanceLogDTO dto);

    AssetMaintenanceLogDTO getById(Long id);

    List<AssetMaintenanceLogDTO> getAllByOrgId(Long orgId);

    List<AssetMaintenanceLogDTO> getByAssetId(Long assetId);

    void delete(Long id);
}
