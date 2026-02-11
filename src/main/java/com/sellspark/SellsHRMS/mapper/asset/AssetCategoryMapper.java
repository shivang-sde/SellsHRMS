package com.sellspark.SellsHRMS.mapper.asset;

import com.sellspark.SellsHRMS.dto.asset.AssetCategoryDTO;
import com.sellspark.SellsHRMS.entity.asset.AssetCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssetCategoryMapper {

    @Mapping(target = "orgId", source = "organisation.id")
    AssetCategoryDTO toDTO(AssetCategory entity);
}
