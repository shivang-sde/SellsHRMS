package com.sellspark.SellsHRMS.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller; // Import the @Controller annotation
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller // <-- ADD THIS ANNOTATION
@RequiredArgsConstructor
public class LoginPageController {

    private final OrganisationRepository organisationRepository;

    @GetMapping({ "/", "/login", "/new-login" })
    public String loginPage(HttpServletRequest request, Model model) {
        String domain = extractDomain(request);

        Organisation org = organisationRepository.findByDomain(domain).orElse(null);

        // This will resolve to /WEB-INF/views/auth/login.jsp

        if (org != null && org.getLogoUrl() != null) {
            model.addAttribute("orgLogo", org.getLogoUrl());
            model.addAttribute("orgName", org.getName());
        }
        // else {
        // model.addAttribute("orgLogo", "/img/sellsparkLogo.png"); // fallback
        // model.addAttribute("orgName", "Sellspark HRMS");
        // }

        return "auth/new_login";
    }

    // @GetMapping({ "/new-login" })
    // public String newLoginPage() {
    // // This will resolve to /WEB-INF/views/auth/login.jsp
    // return "auth/new_login";
    // }

    @GetMapping({ "/register", "/superadmin/register" }) // Corrected typo in "/superadmin/regsiter"
    public String registerPage() {
        // This will resolve to /WEB-INF/views/auth/register.jsp
        return "auth/register";
    }

    @GetMapping("/logout")
    public String logoutPage(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }

    private String extractDomain(HttpServletRequest request) {
        String host = request.getServerName().toLowerCase();
        if (host.startsWith("www.")) {
            host = host.substring(4);
        }
        return host;
    }

}
