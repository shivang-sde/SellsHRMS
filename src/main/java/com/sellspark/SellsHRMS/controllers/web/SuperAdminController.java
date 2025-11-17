package com.sellspark.SellsHRMS.controllers.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.sellspark.SellsHRMS.entity.SuperAdmin;

@Controller
@RequestMapping("/sa")
@RequiredArgsConstructor
public class SuperAdminController {

    private final SuperAdminRepositry superAdminRepo;
    private final PasswordEncoder passwordEncoder;
    // private final AuthenticationManager authenticationManager;

    @GetMapping("/register")
    public String RegisterPage() {
        System.out.println("register page");
        return "sa/register";
    }

    @PostMapping("/register")
    public String registerSuperAdmin(@RequestParam String email,
            @RequestParam String password,
            Model model) {

        if (superAdminRepo.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Super Admin already exists!");
            return "sa/register";
        }

        SuperAdmin superAdmin = SuperAdmin.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .build();

        superAdminRepo.save(superAdmin);
        model.addAttribute("success", "Super Admin registered successfully!");
        return "sa/login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        System.out.println("login page");
        return "sa/login";
    }

    // @PostMapping("/login")
    // public String login(@RequestParam String email,
    // @RequestParam String password,
    // Model model) {

    // try {
    // UsernamePasswordAuthenticationToken authToken =
    // new UsernamePasswordAuthenticationToken(email, password);

    // authenticationManager.authenticate(authToken);

    // return "redirect:/sa/dashboard";

    // } catch (Exception e) {

    // model.addAttribute("error", "Invalid email or password!");
    // return "sa/login";
    // }
    // }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "sa/dashboard";
    }
}
