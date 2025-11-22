package com.sellspark.SellsHRMS.config;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Builder
public class UserPrincipal implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String email;
    private String systemRole;
    private Long organisationId;

    // Set<String> is serializable because String is serializable
    private Set<String> permissions;
}
