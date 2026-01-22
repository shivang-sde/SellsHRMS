package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.User;
import com.sellspark.SellsHRMS.repository.EmployeeRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.repository.RoleRepository;
import com.sellspark.SellsHRMS.repository.UserRepository;
import com.sellspark.SellsHRMS.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepo;
    private final OrganisationRepository orgRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean matchesPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPasswordHash());
    }

    @Override
    public User createUser(String email, String rawPassword, String systemRole, String roleName, Long organisationId) {

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered: " + email);
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setIsActive(true);
        user.setSystemRole(User.SystemRole.valueOf(systemRole));

        if(systemRole.equals("ORG_ADMIN")){
            user.setOrgRole(
                roleRepo.findByOrganisationIdAndNameIgnoreCase(1L, roleName)
                        .orElseThrow(() -> new RuntimeException("Role(ORG ADMIN) not found: " + roleName)));
        }else{
            user.setOrgRole(
                roleRepo.findByOrganisationIdAndNameIgnoreCase(organisationId, roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)));
        }

        // Assign Organisation
        user.setOrganisation(
                orgRepo.findById(organisationId)
                        .orElseThrow(() -> new RuntimeException("Organisation not found: " + organisationId)));

        return userRepository.save(user);
    }

    @Override
    public User createEmpUser(Long employeeId, String email, String rawPassword, String roleName, Long organisationId) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered: " + email);
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setIsActive(true);
        user.setSystemRole(User.SystemRole.EMPLOYEE);

        user.setOrgRole(
                roleRepo.findByOrganisationIdAndNameIgnoreCase(organisationId, roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)));
        // Assign Organisation
        user.setOrganisation(
                orgRepo.findById(organisationId)
                        .orElseThrow(() -> new RuntimeException("Organisation not found: " + organisationId)));

        // Set Employee link
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));
        user.setEmployee(emp);

        return userRepository.save(user);
    }

    @Override
    public User getCurrentUser() {
        // This is a placeholder implementation.
        // In a real application, you would retrieve the currently authenticated user from the security context.
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
