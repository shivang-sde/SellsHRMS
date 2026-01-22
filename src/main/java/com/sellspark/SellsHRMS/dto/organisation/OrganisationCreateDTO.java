package com.sellspark.SellsHRMS.dto.organisation;

import java.time.LocalDate;

import com.sellspark.SellsHRMS.dto.admin.OrgAdminCreateDTO;
import lombok.Data;

@Data
public class OrganisationCreateDTO {
    private String name;
    private String domain;
    private String logoUrl;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private String country;
    private String pan;
    private String tan;
    private Integer maxEmployees;
    private LocalDate validity;

    // If present, create org admin together
    private OrgAdminCreateDTO admin;
}
