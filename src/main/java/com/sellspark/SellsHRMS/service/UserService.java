package com.sellspark.SellsHRMS.service;

import com.sellspark.SellsHRMS.entity.User;

import jakarta.security.auth.message.AuthException;

public interface UserService {
    User findByEmail(String email);

    boolean matchesPassword(User user, String rawPassword);

    void deactivateUser(Long userId);

    void activateUser(Long userId);

    User createUser(String email, String rawPassword, String systemRole, String roleName, Long organisationId);

    // User createOrgAdminUser(String email, String rawPassword, String systemRole,
    // Long organisationId);

    User createEmpUser(Long employeeId, String email, String rawPassword, String roleName, Long organisationId);

    boolean existsByEmail(String email);

    void changePassword(String email, String currentPassword, String newPassword);

    User getCurrentUser();
}
