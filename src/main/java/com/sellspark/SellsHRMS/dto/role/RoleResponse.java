package com.sellspark.SellsHRMS.dto.role;

import java.util.List;

import com.sellspark.SellsHRMS.dto.permission.PermissionDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleResponse {
    private Long id;
    private String name;
    private String description;
    private Long organisationId;
    private List<PermissionDTO> permissions;

    private Long designationId;
    private String designationTitle;
}
