package com.sellspark.SellsHRMS.service.asset;

import com.sellspark.SellsHRMS.dto.asset.VendorDTO;
import java.util.List;

public interface VendorService {
    VendorDTO create(VendorDTO dto);

    VendorDTO getById(Long id);

    List<VendorDTO> getAllByOrgId(Long orgId);

    VendorDTO update(Long id, VendorDTO dto);

    void delete(Long id);
}
