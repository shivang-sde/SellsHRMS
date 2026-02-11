package com.sellspark.SellsHRMS.service.asset;

import com.sellspark.SellsHRMS.dto.asset.AssetAssignmentDTO;
import java.util.List;

public interface AssetAssignmentService {
    List<AssetAssignmentDTO> getAllByOrgId(Long orgId);

    List<AssetAssignmentDTO> getByAssetId(Long assetId);

    List<AssetAssignmentDTO> getByEmployeeId(Long employeeId);

    AssetAssignmentDTO getById(Long id);
}
