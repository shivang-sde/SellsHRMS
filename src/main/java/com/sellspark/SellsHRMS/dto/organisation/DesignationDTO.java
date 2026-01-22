package com.sellspark.SellsHRMS.dto.organisation;

import lombok.Data;

@Data
public class DesignationDTO {
    private Long id;
    private String title;
    private String description;
    private Long departmentId;
    private String departmentName;

    private Long roleId;       
    private String roleName;  

    private Long orgId;
}


