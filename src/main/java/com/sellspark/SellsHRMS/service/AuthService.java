package com.sellspark.SellsHRMS.service;

import com.sellspark.SellsHRMS.entity.User;
import com.sellspark.SellsHRMS.repository.UserRepository;
import com.sellspark.SellsHRMS.config.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final AccessService accessService;
    private final PasswordEncoder passwordEncoder;

    public UserPrincipal authenticate(String email, String rawPassword,
            HttpServletRequest request) {

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null || !passwordEncoder.matches(rawPassword, user.getPasswordHash()))
            return null;

        user.setLastLogin(LocalDateTime.now());
        log.info("user attempted to login at {}", LocalDateTime.now());
        userRepository.save(user);


        if (user.getIsActive() == null ||  Boolean.FALSE.equals(user.getIsActive()))
            return null;

        String userName = resolveUserDisplayName(user);
            

        // Load permissions dynamically
        Set<String> perms = accessService.getPermissionsForUser(user.getId());
        
        UserPrincipal principal = UserPrincipal.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(userName)
                .systemRole(user.getSystemRole() != null ? user.getSystemRole().toString() : "USER")
                .orgRole(user.getOrgRole() != null ? user.getOrgRole().getName() : "NONE")
                .organisationId(user.getOrganisation() != null ? user.getOrganisation().getId() : null)
                .permissions(perms)
                .lastLogin(user.getLastLogin())
                .build();

         // determine authorities
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Add system-level enum role (SUPER_ADMIN / ORG_ADMIN / EMPLOYEE)
        if (user.getSystemRole() != null)
            authorities.add(new SimpleGrantedAuthority(user.getSystemRole().name()));

         // Add org-level role (e.g., HR, MANAGER)
        if (user.getOrgRole() != null)
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getOrgRole().getName().toUpperCase()));

        
        // set Spring Security context (so .authenticated() checks pass), Add all permission codes (LEAVE_VIEW_SELF, etc.)
        authorities.addAll(perms.stream()
                .map(SimpleGrantedAuthority::new)
                .toList());

        // Bind to SecurityContext
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
        session.setAttribute("LAST_LOGIN", user.getLastLogin());
        session.setAttribute("EMAIL", user.getEmail());
        session.setAttribute("USER_NAME", userName);
        session.setAttribute("EMP_ID", user.getEmployee() != null ? user.getEmployee().getId() : null); 
        session.setAttribute("SYSTEM_ROLE", principal.getSystemRole());
        session.setAttribute("ORG_ROLE", principal.getOrgRole());
        session.setAttribute("ORG_ID", principal.getOrganisationId());
        session.setAttribute("PERMISSIONS", perms);
        // session.setAttribute("MODULES", accessService.getModuleCodesForUser(user.getId()));

        // log.info("Authenticated [{}] | systemRole={} | orgRole={} | orgId={}",
        //         user.getEmail(), principal.getSystemRole(), principal.getOrgRole(), principal.getOrganisationId());

        return principal;
    }

    private String resolveUserDisplayName(User user) {
        if (user.getSystemRole() == User.SystemRole.SUPER_ADMIN)
            return "Super Admin";
        if (user.getSystemRole() == User.SystemRole.ORG_ADMIN && user.getOrganisation() != null
                && user.getOrganisation().getOrgAdmin() != null)
            return user.getOrganisation().getOrgAdmin().getFullName();
        if (user.getSystemRole() == User.SystemRole.EMPLOYEE && user.getEmployee() != null)
            return user.getEmployee().getFirstName() + " " + user.getEmployee().getLastName();
        return "User";
    }

}
