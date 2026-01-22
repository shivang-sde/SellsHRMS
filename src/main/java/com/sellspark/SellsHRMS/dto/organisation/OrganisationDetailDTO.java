package com.sellspark.SellsHRMS.dto.organisation;

import java.time.LocalDate;

import com.sellspark.SellsHRMS.dto.admin.OrgAdminSummaryDTO;
import lombok.Data;

@Data
public class OrganisationDetailDTO {
    private Long id;
    private String name;
    private String domain;
    private Boolean isActive;
    private String logoUrl;

    private String contactEmail;
    private String contactPhone;

    private String address;
    private String country;

    private Integer maxEmployees;
    private LocalDate validity;

    private OrgAdminSummaryDTO admin;
}
