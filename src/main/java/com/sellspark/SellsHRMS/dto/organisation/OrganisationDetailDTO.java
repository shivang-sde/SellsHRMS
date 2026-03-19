package com.sellspark.SellsHRMS.dto.organisation;

import java.time.LocalDate;

import com.sellspark.SellsHRMS.dto.admin.OrgAdminSummaryDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganisationDetailDTO {
    private Long id;
    private String name;
    private String domain;
    private String timeZone;
    private String prefix;
    private Boolean isActive;
    private String logoUrl;

    private String contactEmail;
    private String contactPhone;

    private String address;
    private String country;

    private Long totalEmployees;
    private Long totalDepartments;

    private Integer maxEmployees;
    private LocalDate validity;

    private OrgAdminSummaryDTO admin;
}
