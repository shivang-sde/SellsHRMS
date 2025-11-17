package com.sellspark.SellsHRMS.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sellspark.SellsHRMS.entity.User;
import com.sellspark.SellsHRMS.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public User login(String email, String password) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!encoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }
}
