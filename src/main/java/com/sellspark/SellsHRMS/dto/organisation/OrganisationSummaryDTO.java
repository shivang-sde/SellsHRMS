package com.sellspark.SellsHRMS.dto.organisation;

import java.time.LocalDate;

import lombok.Data;

@Data
public class OrganisationSummaryDTO {

    private Long id;
    private String name;
    private String domain;
    private Boolean isActive;
    private LocalDate validity;
    private Integer maxEmployees;
    private String adminName;
    private String adminEmail;
}
