package com.sellspark.SellsHRMS.dto.admin;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OrgAdminSummaryDTO {
    private Long id;
    private String fullName;
    private String email;
    private Boolean isActive;
    private LocalDateTime lastLogin;

    private Long organisationId;
    private String organisationName;
}
