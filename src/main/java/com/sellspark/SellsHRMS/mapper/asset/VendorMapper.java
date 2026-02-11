package com.sellspark.SellsHRMS.mapper.asset;

import com.sellspark.SellsHRMS.dto.asset.VendorDTO;
import com.sellspark.SellsHRMS.entity.asset.Vendor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VendorMapper {

    @Mapping(target = "orgId", source = "organisation.id")
    VendorDTO toDTO(Vendor entity);
}
