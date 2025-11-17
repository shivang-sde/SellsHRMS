package com.sellspark.SellsHRMS.security;

import com.sellspark.SellsHRMS.superadmin.SuperAdminDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CombinedUserDetailsService implements UserDetailsService {

    private final SuperAdminDetailService superAdminDetailService;
    private final UserDetailServiceImpl userDetailService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1️⃣ Try Super Admin first
        try {
            return superAdminDetailService.loadUserByUsername(username);
        } catch (Exception ignored) {
        }

        // 2️⃣ Try Tenant User
        try {
            return userDetailService.loadUserByUsername(username);
        } catch (Exception ignored) {
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }
}
