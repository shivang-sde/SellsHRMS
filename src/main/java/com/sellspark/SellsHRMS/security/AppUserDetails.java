package com.sellspark.SellsHRMS.security;

import com.sellspark.SellsHRMS.superadmin.SuperAdmin;
import com.sellspark.SellsHRMS.entity.User;
import lombok.Getter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
public class AppUserDetails implements UserDetails {

    private final Object principalEntity;

    public AppUserDetails(SuperAdmin sa) {
        this.principalEntity = sa;
    }

    public AppUserDetails(User user) {
        this.principalEntity = user;
    }

    private String getRoleName() {
        if (principalEntity instanceof SuperAdmin) {
            var sa = (SuperAdmin) principalEntity;
            return sa.getRole() != null ? sa.getRole().getName() : null;
        } else {
            var user = (User) principalEntity;
            return user.getRole() != null ? user.getRole().getName() : null;
        }
    }

    @Override
    public Collection<SimpleGrantedAuthority> getAuthorities() {
        var role = getRoleName();
        if (role == null)
            return List.of();
        return List.of(new SimpleGrantedAuthority("ROLE_" + role)); // ROLE_SUPER_ADMIN etc.
    }

    @Override
    public String getPassword() {
        if (principalEntity instanceof SuperAdmin)
            return ((SuperAdmin) principalEntity).getPasswordHash();
        return ((User) principalEntity).getPasswordHash();
    }

    @Override
    public String getUsername() {
        if (principalEntity instanceof SuperAdmin)
            return ((SuperAdmin) principalEntity).getEmail();
        return ((User) principalEntity).getEmail();
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
        if (principalEntity instanceof SuperAdmin)
            return true;
        return Boolean.TRUE.equals(((User) principalEntity).getIsActive());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AppUserDetails))
            return false;
        AppUserDetails that = (AppUserDetails) o;
        return Objects.equals(this.getUsername(), that.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername());
    }
}
