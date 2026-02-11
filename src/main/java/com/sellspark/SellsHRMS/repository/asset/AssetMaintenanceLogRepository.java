package com.sellspark.SellsHRMS.repository.asset;

import com.sellspark.SellsHRMS.entity.asset.AssetMaintenanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssetMaintenanceLogRepository extends JpaRepository<AssetMaintenanceLog, Long> {
    List<AssetMaintenanceLog> findByOrganisation_Id(Long orgId);

    List<AssetMaintenanceLog> findByAsset_Id(Long assetId);
}
