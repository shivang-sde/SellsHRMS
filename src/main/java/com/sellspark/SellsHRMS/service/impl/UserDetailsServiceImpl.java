package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.entity.User;
import com.sellspark.SellsHRMS.repository.UserRepository;
import com.sellspark.SellsHRMS.service.AccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final AccessService accessService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Set<String> perms = accessService.getPermissionsForUser(u.getId()); // implement in AccessService
        var authorities = perms.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                u.getEmail(),
                u.getPasswordHash(),
                u.getIsActive() != null && u.getIsActive(),
                true, true, true,
                authorities);
    }
}
