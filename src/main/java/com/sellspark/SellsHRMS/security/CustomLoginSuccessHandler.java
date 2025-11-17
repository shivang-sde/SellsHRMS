package com.sellspark.SellsHRMS.security;

import jakarta.servlet.http.*;
import org.springframework.security.core.*;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        String role = authentication.getAuthorities()
                .stream().findFirst().get().getAuthority();

        switch (role) {

            case "ROLE_SUPER_ADMIN":
                response.sendRedirect("/sa/dashboard");
                break;

            case "ROLE_ADMIN":
                response.sendRedirect("/admin/dashboard");
                break;

            case "ROLE_HR":
                response.sendRedirect("/hr/dashboard");
                break;

            case "ROLE_EMPLOYEE":
                response.sendRedirect("/user/dashboard");
                break;

            default:
                response.sendRedirect("/login?error=role");
        }
    }
}
