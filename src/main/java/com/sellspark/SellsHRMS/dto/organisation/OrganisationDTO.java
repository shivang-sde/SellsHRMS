package com.sellspark.SellsHRMS.dto.organisation;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganisationDTO {
    private Long id;
    private String name;
    private String domain;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private String country;
    private String logoUrl;
    private String pan;
    private String tan;
    private Integer maxEmployees;
    private Boolean isActive;
    private LocalDate validity;
    private String suspendedReason;
    private String adminFullName;
    private String adminEmail;
    private String adminPassword;
}
