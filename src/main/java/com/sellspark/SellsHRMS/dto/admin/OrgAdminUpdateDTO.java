package com.sellspark.SellsHRMS.dto.admin;

import lombok.Data;

@Data
public class OrgAdminUpdateDTO {
    private String fullName;
    private String email;
    private Boolean isActive;
}
