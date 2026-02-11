package com.sellspark.SellsHRMS.mapper.asset;

import com.sellspark.SellsHRMS.dto.asset.AssetMaintenanceLogDTO;
import com.sellspark.SellsHRMS.entity.asset.AssetMaintenanceLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssetMaintenanceLogMapper {

    @Mapping(target = "assetId", source = "asset.id")
    @Mapping(target = "assetCode", source = "asset.assetCode")
    @Mapping(target = "assetName", source = "asset.name")
    @Mapping(target = "orgId", source = "organisation.id")
    AssetMaintenanceLogDTO toDTO(AssetMaintenanceLog entity);
}
