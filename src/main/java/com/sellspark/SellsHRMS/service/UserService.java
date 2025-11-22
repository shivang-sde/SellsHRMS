package com.sellspark.SellsHRMS.service;

import com.sellspark.SellsHRMS.entity.User;

public interface UserService {
    User findByEmail(String email);

    boolean matchesPassword(User user, String rawPassword);

    User createUser(String email, String rawPassword, String roleName, Long organisationId);

    boolean existsByEmail(String email);
}
