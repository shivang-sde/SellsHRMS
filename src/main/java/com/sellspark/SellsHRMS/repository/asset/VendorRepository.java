package com.sellspark.SellsHRMS.repository.asset;

import com.sellspark.SellsHRMS.entity.asset.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    List<Vendor> findByOrganisation_Id(Long orgId);
}
