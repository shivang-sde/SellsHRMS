package com.sellspark.SellsHRMS.repository.asset;

import com.sellspark.SellsHRMS.entity.asset.AssetAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AssetAssignmentRepository extends JpaRepository<AssetAssignment, Long> {
    List<AssetAssignment> findByOrganisation_Id(Long orgId);

    List<AssetAssignment> findByAsset_Id(Long assetId);

    Optional<AssetAssignment> findByAsset_IdAndActiveFlagTrue(Long assetId);

    List<AssetAssignment> findByEmployee_Id(Long employeeId);
}
