package com.sellspark.SellsHRMS.dto;

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
public class OrgUpdateRequest {

    private String name;
    private String domain;
    private String logoUrl;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private String country;
    private String pan;
    private String tan;

}
