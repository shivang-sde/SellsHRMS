package com.sellspark.SellsHRMS.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OrgAdminDTO {
    private Long organisationId;
    private String fullName;
    private String email;
    private String password;
}
