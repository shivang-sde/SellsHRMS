package com.sellspark.SellsHRMS.dto.organisation;

import java.time.LocalDate;

import lombok.Data;

@Data
public class OrganisationUpdateDTO {
    private String name;
    private String domain;
    private String logoUrl;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private String country;
    private Integer maxEmployees;
    private LocalDate validity;
    private Boolean isActive;
}
