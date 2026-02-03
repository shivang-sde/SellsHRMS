package com.sellspark.SellsHRMS.config;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@Builder
public class UserPrincipal implements UserDetails {

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
    private Collection<? extends GrantedAuthority> authorities;
    private String password;
    private boolean active;

    public boolean hasPermission(String code) {
        return permissions != null && permissions.contains(code);
    }

    public boolean hasAnyPermission(String... codes) {
        if (permissions == null)
            return false;
        for (String code : codes)
            if (permissions.contains(code))
                return true;
        return false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
