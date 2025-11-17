package com.sellspark.SellsHRMS.controllers.web;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, HttpSession session, Model model) {

        model.addAttribute("modules", session.getAttribute("modules"));
        model.addAttribute("user", auth.getName());

        return "dashboard";
    }
}
