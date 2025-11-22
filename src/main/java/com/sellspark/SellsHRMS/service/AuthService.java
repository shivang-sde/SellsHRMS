package com.sellspark.SellsHRMS.service;

import com.sellspark.SellsHRMS.entity.User;
import com.sellspark.SellsHRMS.repository.UserRepository;
import com.sellspark.SellsHRMS.config.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AccessService accessService;
    private final PasswordEncoder passwordEncoder;

    public UserPrincipal authenticate(String email, String rawPassword,
            HttpServletRequest request) {

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null)
            return null;
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash()))
            return null;
        if (user.getIsActive() == null || !user.getIsActive())
            return null;

        // Load permissions dynamically
        Set<String> perms = accessService.getPermissionsForUser(user.getId());
        UserPrincipal principal = UserPrincipal.builder()
                .id(user.getId())
                .email(user.getEmail())
                .systemRole(user.getRole() != null ? user.getRole().getName() : "USER")
                .organisationId(user.getOrganisation() != null ? user.getOrganisation().getId() : null)
                .permissions(perms)
                .build();

        var authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(principal.getSystemRole()));
        // set Spring Security context (so .authenticated() checks pass)
        authorities.addAll(perms.stream()
                .map(SimpleGrantedAuthority::new)
                .toList());

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null,
                authorities);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        // store context into HTTP session (for subsequent requests)
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        // also store convenience items for JSPs (modules + role)
        session.setAttribute("USER_ID", user.getId());
        session.setAttribute("EMAIL", user.getEmail());
        session.setAttribute("SYSTEM_ROLE", principal.getSystemRole());
        session.setAttribute("PERMISSIONS", perms);
        session.setAttribute("MODULES", accessService.getModuleCodesForUser(user.getId()));

        return principal;
    }

}
