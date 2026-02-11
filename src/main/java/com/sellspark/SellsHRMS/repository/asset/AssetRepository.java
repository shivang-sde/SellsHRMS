package com.sellspark.SellsHRMS.repository.asset;

import com.sellspark.SellsHRMS.entity.asset.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByOrganisation_Id(Long orgId);

    Optional<Asset> findByAssetCodeAndOrganisation_Id(String assetCode, Long orgId);

    boolean existsByAssetCodeAndOrganisation_Id(String assetCode, Long orgId);
}
