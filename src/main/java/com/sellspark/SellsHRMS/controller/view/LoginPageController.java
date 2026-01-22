package com.sellspark.SellsHRMS.controller.view;

import org.springframework.stereotype.Controller; // Import the @Controller annotation
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller // <-- ADD THIS ANNOTATION
public class LoginPageController {

    @GetMapping({ "/", "/login" })
    public String loginPage() {
        // This will resolve to /WEB-INF/views/auth/login.jsp
        return "auth/new_login";
    }

    @GetMapping({"/new-login" })
    public String newLoginPage() {
        // This will resolve to /WEB-INF/views/auth/login.jsp
        return "auth/new_login";
    }

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
        return "redirect:/login";
}

}
