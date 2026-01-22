package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.dto.LoginRequest;
import com.sellspark.SellsHRMS.dto.LoginResponse;
import com.sellspark.SellsHRMS.entity.User;
import com.sellspark.SellsHRMS.exception.HRMSException;
import com.sellspark.SellsHRMS.repository.UserRepository;
import com.sellspark.SellsHRMS.service.AuthService;
import com.sellspark.SellsHRMS.service.SuperAdminService;
import com.sellspark.SellsHRMS.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthService authService;
    private final SuperAdminService superAdminService;
    private final UserRepository userRepo;
    private final UserService userService;

    @PostMapping("/register-superadmin")
    public ResponseEntity<?> registerSuperAdmin(@RequestBody LoginRequest request) {

        var user = superAdminService.create(request.getEmail(), request.getPassword());

        System.out.println(user);
        return ResponseEntity.ok(user);
    }

    // --------------------------
    // LOGIN
    // --------------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
            HttpServletRequest httpRequest, HttpSession session) {

        var principal = authService.authenticate(
                request.getEmail(),
                request.getPassword(),
                httpRequest);

        if (principal == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid email or password"));
        }
        
        User user = userRepo.findById(principal.getId()).orElseThrow(() ->  new HRMSException("User not found with id " + principal.getId() + " .", "USER_NOT_FOUND", HttpStatus.NOT_FOUND));

        user.setLastLogin(LocalDateTime.now());

        userRepo.save(user);
        
    

        LoginResponse response = LoginResponse.builder()
                .id(principal.getId())
                .email(principal.getEmail())
                .systemRole(principal.getSystemRole())
                .organisationId(principal.getOrganisationId())
                .permissions(principal.getPermissions())
                .modules(null) // optional — can fill if needed
                .build();

        // Store user info in session
        session.setAttribute("USER_ID", principal.getId());
        session.setAttribute("EMAIL", principal.getEmail());
        session.setAttribute("SYSTEM_ROLE", principal.getSystemRole());
        session.setAttribute("OrgId", principal.getOrganisationId());

    
        return ResponseEntity.ok(response);
    }

    // --------------------------
    // LOGOUT
    // --------------------------
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    // --------------------------
    // CHECK SESSION
    // --------------------------
    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        if (session == null || session.getAttribute("USER_ID") == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Not logged in"));
        }

        return ResponseEntity.ok(
                Map.of(
                        "id", session.getAttribute("USER_ID"),
                        "email", session.getAttribute("EMAIL"),
                        "role", session.getAttribute("SYSTEM_ROLE"),
                        "permissions", session.getAttribute("PERMISSIONS"),
                        "modules", session.getAttribute("MODULES")));
    }
}
