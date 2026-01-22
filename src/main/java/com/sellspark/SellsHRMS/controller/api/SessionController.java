 package com.sellspark.SellsHRMS.controller.api;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
public class SessionController {

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        return ResponseEntity.ok(
                Map.of(
                        "id", session.getAttribute("USER_ID"),
                        "role", session.getAttribute("ROLE"),
                        "email", session.getAttribute("EMAIL"),
                        "displayName", session.getAttribute("NAME"),
                        "systemRole", session.getAttribute("SYSTEM_ROLE")));
    }
}
