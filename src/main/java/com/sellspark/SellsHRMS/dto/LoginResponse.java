package com.sellspark.SellsHRMS.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private Long id;
    private String email;
    private String systemRole; // SUPER_ADMIN, ORG_ADMIN, USER
    private Long organisationId;
    private Set<String> permissions; // permission codes or module codes
    private Set<String> modules; // optional convenience
}
