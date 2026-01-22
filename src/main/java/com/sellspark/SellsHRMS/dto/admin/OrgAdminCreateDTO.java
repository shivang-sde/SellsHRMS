package com.sellspark.SellsHRMS.dto.admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrgAdminCreateDTO {
    private String fullName;
    private String email;
    private String password;
}
