package com.sellspark.SellsHRMS.mapper.asset;

import com.sellspark.SellsHRMS.dto.asset.AssetDTO;
import com.sellspark.SellsHRMS.entity.asset.Asset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AssetMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "vendorId", source = "vendor.id")
    @Mapping(target = "vendorName", source = "vendor.name")
    @Mapping(target = "assignedToId", source = "assignedTo.id")
    @Mapping(target = "assignedToName", source = "assignedTo", qualifiedByName = "employeeFullName")
    @Mapping(target = "orgId", source = "organisation.id")
    AssetDTO toDTO(Asset entity);

    @Named("employeeFullName")
    default String employeeFullName(com.sellspark.SellsHRMS.entity.Employee emp) {
        if (emp == null)
            return null;
        return emp.getFirstName() + " " + emp.getLastName();
    }
}
