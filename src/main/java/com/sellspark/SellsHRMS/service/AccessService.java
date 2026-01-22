package com.sellspark.SellsHRMS.service;

import com.sellspark.SellsHRMS.entity.*;
import com.sellspark.SellsHRMS.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccessService {

   
    private final OrganisationModuleRepository orgModuleRepo;
    private final RoleModuleRepository roleModuleRepo;
    private final UserModuleRepository userModuleRepo;
    private final UserRepository userRepository;


    // earlier method (module codes)
    public List<String> getModuleCodesForUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null)
            return List.of();

        Long orgId = user.getOrganisation().getId();

        Set<String> orgCodes = orgModuleRepo.findByOrganisationId(orgId).stream()
                .map(om -> om.getModule().getCode()).collect(Collectors.toSet());

        Set<String> roleCodes = new HashSet<>();
        if (user.getOrgRole() != null) {
            roleCodes = roleModuleRepo.findByRoleId(user.getOrgRole().getId()).stream()
                    .map(rm -> rm.getModule().getCode()).collect(Collectors.toSet());
        }

        Set<String> base = new HashSet<>();
        if (!roleCodes.isEmpty()) {
            for (String c : roleCodes)
                if (orgCodes.contains(c))
                    base.add(c);
        } else {
            base.addAll(orgCodes);
        }

        // user overrides
        for (UserModule um : userModuleRepo.findByUserId(userId)) {
            if (um.isGranted())
                base.add(um.getModule().getCode());
            else
                base.remove(um.getModule().getCode());
        }
        List<String> result = new ArrayList<>(base);
        Collections.sort(result);
        return result;
    }

    // permissions: gather permission codes from role -> role.permissions and map to
    // set
    // authorities = [systemRole, ROLE_<orgRoleName>, <permissionCodes>]
    public Set<String> getPermissionsForUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getOrgRole() == null) return Set.of();
            

        // Role role = user.getOrgRole();
        // String systemRole = user.getSystemRole().toString();
        // Set<String> perms = new HashSet<>();

        // if (role != null && role.getPermissions() != null) {
        //     perms.addAll(role.getPermissions().stream()
        //             .map(Permission::getCode)
        //             .collect(Collectors.toSet()));
        // }


        return user.getOrgRole().getPermissions().stream()
               .map(Permission::getCode)
               .collect(Collectors.toSet());

        // optional: apply per-user explicit permission grants/revokes (if you want)
        // we didn't create a user-permission table in this iteration; add if needed

        // optionally map module codes to coarse-grained MODULE_* permissions for UI
        // List<String> modules = getModuleCodesForUser(userId);
        // for (String m : modules)
        //     perms.add("MODULE_" + m);

        // return perms;
    }
}
