package com.sellspark.SellsHRMS.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganisationRequest {
    private String name;
    private String domain;
    private String contactEmail;
    private String contactPhone;
    private Integer maxEmployees;
}
