package com.sellspark.SellsHRMS.superadmin;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import lombok.Getter;

@Getter
public class SuperAdminDetails implements UserDetails {

    private final SuperAdmin superAdmin;

    public SuperAdminDetails(SuperAdmin superAdmin) {
        this.superAdmin = superAdmin;
    }

    @Override
    public List<SimpleGrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"));
    }

    @Override
    public String getPassword() {
        return superAdmin.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return superAdmin.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
