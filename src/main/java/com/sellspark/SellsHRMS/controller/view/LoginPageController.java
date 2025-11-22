package com.sellspark.SellsHRMS.controller.view;

import org.springframework.stereotype.Controller; // Import the @Controller annotation
import org.springframework.web.bind.annotation.GetMapping;

@Controller // <-- ADD THIS ANNOTATION
public class LoginPageController {

    @GetMapping({ "/", "/login" })
    public String loginPage() {
        // This will resolve to /WEB-INF/views/auth/login.jsp
        return "auth/login";
    }

    @GetMapping({ "/register", "/superadmin/register" }) // Corrected typo in "/superadmin/regsiter"
    public String registerPage() {
        // This will resolve to /WEB-INF/views/auth/register.jsp
        return "auth/register";
    }

}
