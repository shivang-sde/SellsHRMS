package com.sellspark.SellsHRMS.security;

import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final DashboardModuleService dashboardModuleService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication auth) throws java.io.IOException {

        // Store modules in session (SuperAdmin or Tenant User)
        request.getSession().setAttribute(
                "modules",
                dashboardModuleService.getModulesForUser(auth.getName()));

        response.sendRedirect("/dashboard");
    }
}
