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
    private final UserRepository userRepository;

    // earlier method (module codes)
    public List<String> getModuleCodesForUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null)
            return List.of();

        Long orgId = user.getOrganisation().getId();

        List<String> orgCodes = orgModuleRepo.findActiveModuleCodesByOrganisationId(orgId);

        Set<String> base = new HashSet<>();
        base.addAll(orgCodes);

        List<String> result = new ArrayList<>(base);
        Collections.sort(result);
        return result;
    }

    // permissions: gather permission codes from role -> role.permissions and map to
    // set
    // authorities = [systemRole, ROLE_<orgRoleName>, <permissionCodes>]
    public Set<String> getPermissionsForUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getOrgRole() == null)
            return Set.of();
        return user.getOrgRole().getPermissions().stream()
                .map(Permission::getCode)
                .collect(Collectors.toSet());

    }
}
