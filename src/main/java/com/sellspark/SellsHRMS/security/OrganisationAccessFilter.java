package com.sellspark.SellsHRMS.security;

import com.sellspark.SellsHRMS.entity.User;
import com.sellspark.SellsHRMS.config.UserPrincipal;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

/**
 * Ensures that Org Admins or Employees can perform actions only
 * if their organisation is active and has a valid license.
 * Super Admin bypasses these checks.
 *
 * Also enforces document verification gate:
 * Users from organisations with fewer than 2 verified documents
 * are redirected to the verification page.
 */
@Slf4j
@Component
public class OrganisationAccessFilter extends OncePerRequestFilter {

    private final UserRepository userRepo;

    public OrganisationAccessFilter(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // ANSI colors for logs
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String BLUE = "\u001B[34m";
    private static final String RESET = "\u001B[0m";

    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/auth/",
            "/api/superadmin",
            "/superadmin",
            "/login",
            "/new-login",
            "/register",
            "/css",
            "/plugins",
            "/bundles",
            "/js",
            "/images",
            "/img",
            "/error",
            "/favicon",
            "/actuator",
            "/actuator/health",
            "/actuator/info",
            "/api/test/",
            "/WEB-INF/views",
            "/actuator/metrics/**",
            "/verify/",
            "/api/verify/",
            "/public/",
            "/api/public/",
            "/terms-and-conditions");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            // 🔍 Detect API or normal request
            String path = request.getRequestURI();
            if (path.startsWith("/api/auth/")) {
                filterChain.doFilter(request, response);
                return;
            }
            boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
            boolean isApi = path.startsWith("/api/");

            log.warn("{}[SESSION_EXPIRED_DETECTED]{} Path={} | isApi={} | isAjax={}",
                    YELLOW, RESET, path, isApi, isAjax);

            if (isAjax || isApi) {
                // Return JSON for API calls
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(
                        "{\"errorCode\":\"SESSION_EXPIRED\",\"message\":\"Your session has expired. Please log in again.\"}");
            } else {
                // Redirect to login page for normal users
                String redirectUrl = request.getContextPath() + "/new-login?session=expired";
                log.info("{}[SESSION_EXPIRED_REDIRECT]{} -> {}", BLUE, RESET, redirectUrl);
                response.sendRedirect(redirectUrl);
            }
            return;
        }

        Object principal = auth.getPrincipal();
        String email;

        if (principal instanceof UserPrincipal customPrincipal) {
            email = customPrincipal.getEmail();
        } else if (principal instanceof org.springframework.security.core.userdetails.User springUser) {
            email = springUser.getUsername();
        } else if (principal instanceof String s) {
            email = s;
        } else {
            log.warn("{}[UNKNOWN_PRINCIPAL]{} Type: {}", YELLOW, RESET, principal.getClass());
            filterChain.doFilter(request, response);
            return;
        }

        User user = userRepo.findByEmailWithOrganisation(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (user == null || Boolean.FALSE.equals(user.getIsActive())) {
            SecurityContextHolder.clearContext();
            request.getSession().invalidate();

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("User account is deactivated.");
            return;
        }

        // Super Admin bypass
        if (user.getSystemRole() == User.SystemRole.SUPER_ADMIN) {
            log.info("{}[SUPER_ADMIN_ACCESS]{} {} accessing {}", BLUE, RESET, email, request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        Organisation org = user.getOrganisation();

        if (org == null) {
            log.warn("{}[ORG_NOT_ASSIGNED]{} User {} tried accessing {} but is not assigned to any organisation.",
                    RED, RESET, email, request.getRequestURI());
            handleOrgError(request, response,
                    "You are not assigned to any organisation. Contact administrator.",
                    "ORG_NOT_ASSIGNED");
            return;
        }

        LocalDate today = LocalDate.now();
        log.info("{}[ORG_CHECK]{} User={} Org={} Active={} ValidTill={}",
                BLUE, RESET, email, org.getName(), org.getIsActive(), org.getValidity());

        // ❌ Inactive org
        if (Boolean.FALSE.equals(org.getIsActive())) {
            log.warn("{}[ORG_BLOCKED]{} Access denied for user {} — Org '{}' is inactive.",
                    RED, RESET, user.getEmail(), org.getName());
            handleOrgError(request, response,
                    "Your organisation has been deactivated. Contact support.",
                    "ORG_INACTIVE");
            return;
        }

        // ❌ License expired
        if (org.getValidity() != null && org.getValidity().isBefore(today)) {
            log.warn("{}[ORG_EXPIRED]{} Org '{}' expired on {} — Access denied for user {}.",
                    RED, RESET, org.getName(), org.getValidity(), user.getEmail());
            handleOrgError(request, response,
                    String.format("Your organisation license expired on %s. Please contact support to renew.",
                            org.getValidity()),
                    "ORG_LICENSE_EXPIRED");
            return;
        }

        // ❌ Document verification gate: redirect if fewer than 2 documents verified
        String requestPath = request.getRequestURI();
        if (org.getVerifiedDocumentCount() < 2
                && !requestPath.startsWith("/verify/")
                && !requestPath.startsWith("/api/verify/")
                && !requestPath.startsWith("/api/files/")
                && !requestPath.startsWith("/logout")) {

            log.warn("{}[DOC_VERIFICATION_REQUIRED]{} Org '{}' has only {} verified docs. Redirecting user {}.",
                    YELLOW, RESET, org.getName(), org.getVerifiedDocumentCount(), user.getEmail());

            boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
            boolean isApi = requestPath.startsWith("/api/");

            if (isAjax || isApi) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(
                        "{\"errorCode\":\"DOC_VERIFICATION_REQUIRED\",\"message\":\"Document verification is required before accessing the application.\",\"redirectUrl\":\"/verify/documents\"}");
            } else if (user.getSystemRole() == User.SystemRole.ORG_ADMIN) {
                response.sendRedirect(request.getContextPath() + "/verify/documents");
            } else {
                handleOrgError(request, response,
                        "Document verification is required before accessing the application.",
                        "DOC_VERIFICATION_REQUIRED");
            }
            return;
        }

        // ✅ All good
        log.info("{}[ORG_VALID]{} Org '{}' is active & valid (till {}). Access granted.",
                GREEN, RESET, org.getName(),
                org.getValidity() != null ? org.getValidity() : "∞");

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getRequestURI();
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith) || path.contains("/WEB-INF/views/");
    }

    /**
     * Unified handler — returns JSON for API/AJAX and redirects for JSP requests.
     */
    private void handleOrgError(HttpServletRequest request, HttpServletResponse response,
            String message, String code) throws IOException {

        String path = request.getRequestURI();
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        boolean isApi = path.startsWith("/api/");

        log.info("{}[ORG_ERROR_DETECT]{} Path={} | isAjax={} | isApi={} | Code={}",
                YELLOW, RESET, path, isAjax, isApi, code);

        if (isAjax || isApi) {
            // For API / AJAX calls → return JSON
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(String.format(
                    "{\"errorCode\":\"%s\",\"message\":\"%s\"}",
                    code, message));
            log.error("{}[ORG_ACCESS_DENIED_JSON]{} -> {} : {}", RED, RESET, code, message);
        } else {
            // For normal page requests → redirect to error JSP
            log.error("{}[ORG_ACCESS_DENIED_PAGE]{} Redirecting to /error/license-expired", RED, RESET);
            String redirectUrl = request.getContextPath()
                    + "/error/license-expired?code=" + code
                    + "&msg=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
            response.sendRedirect(redirectUrl);
        }
    }
}
