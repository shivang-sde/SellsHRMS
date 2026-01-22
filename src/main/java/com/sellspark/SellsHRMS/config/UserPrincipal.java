package com.sellspark.SellsHRMS.config;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
public class UserPrincipal implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String email;
    private String systemRole;
    private String orgRole;
    private LocalDateTime lastLogin;
    private Long organisationId;
    private String name;

    // Set<String> is serializable because String is serializable
    private Set<String> permissions;


    public boolean hasPermission(String code){
        return permissions != null && permissions.contains(code);
    }

    public boolean hasAnyPermission(String... codes) {
        if (permissions == null) return false;
        for(String code : codes)
            if (permissions.contains(code)) return true;
        return false;
    }
}
