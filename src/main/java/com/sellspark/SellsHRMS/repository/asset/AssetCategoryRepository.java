package com.sellspark.SellsHRMS.repository.asset;

import com.sellspark.SellsHRMS.entity.asset.AssetCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssetCategoryRepository extends JpaRepository<AssetCategory, Long> {
    List<AssetCategory> findByOrganisation_Id(Long orgId);

    List<AssetCategory> findByOrganisation_IdAndIsActiveTrue(Long orgId);
}
