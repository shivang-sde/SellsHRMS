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
    private String timeZone;
    private String prefix;
    private Integer padding;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private String country;
    private String logoUrl;
    private String aadhar;
    private String pan;
    private String tan;
    private String gst;

    private String aadharUrl;
    private String panUrl;
    private String tanUrl;
    private String gstUrl;

    private boolean isPanVerified;
    private boolean isTanVerified;
    private boolean isGstVerified;
    private boolean isAadharVerified;
    private Integer maxEmployees;
    private Boolean isActive;
    private LocalDate validity;
    private String suspendedReason;
    private String adminFullName;
    private String adminEmail;
    private String adminPassword;
}
