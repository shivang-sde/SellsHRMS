package com.sellspark.SellsHRMS.dto.organisation;

import java.time.LocalDate;

import lombok.Data;

@Data
public class OrganisationPatchRequest {
    private String name;
    private String address;
    private String domain;
    private Boolean isActive;
    private LocalDate validity;
    private Integer maxEmployees;
    private String contactPhone;
    private String contactEmail;
}
