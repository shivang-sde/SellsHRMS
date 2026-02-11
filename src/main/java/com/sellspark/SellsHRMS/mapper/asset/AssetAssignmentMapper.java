package com.sellspark.SellsHRMS.mapper.asset;

import com.sellspark.SellsHRMS.dto.asset.AssetAssignmentDTO;
import com.sellspark.SellsHRMS.entity.asset.AssetAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AssetAssignmentMapper {

    @Mapping(target = "assetId", source = "asset.id")
    @Mapping(target = "assetCode", source = "asset.assetCode")
    @Mapping(target = "assetName", source = "asset.name")
    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeName", source = "employee", qualifiedByName = "empFullName")
    @Mapping(target = "orgId", source = "organisation.id")
    AssetAssignmentDTO toDTO(AssetAssignment entity);

    @Named("empFullName")
    default String empFullName(com.sellspark.SellsHRMS.entity.Employee emp) {
        if (emp == null)
            return null;
        return emp.getFirstName() + " " + emp.getLastName();
    }
}
