package com.sellspark.SellsHRMS.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.service.verification.DocumentVerificationService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Serves the standalone document verification page.
 * This page uses NO main layout (no sidebar) — it's a full-screen wizard.
 */
@Slf4j
@Controller
@RequestMapping("/verify")
@RequiredArgsConstructor
public class VerificationViewController {

    private final DocumentVerificationService verificationService;

    /**
     * Main verification page — served without layout.
     */
    @GetMapping("/documents")
    public String verificationPage(Model model, HttpSession session) {
        Long orgId = (Long) session.getAttribute("ORG_ID");
        String orgName = "";

        if (orgId != null) {
            try {
                var status = verificationService.getVerificationStatus(orgId);
                orgName = status.getOrganisationName();
            } catch (Exception e) {
                log.warn("Could not load org name for verification page: {}", e.getMessage());
            }
        }

        model.addAttribute("pageTitle", "Document Verification");
        model.addAttribute("orgName", orgName);
        model.addAttribute("orgId", orgId);
        // Direct JSP return — NO layout wrapper
        return "verification/verify-documents";
    }

    /**
     * Resumable verification via email token.
     */
    @GetMapping("/resume")
    public String resumeVerification(@RequestParam String token, Model model, HttpSession session) {
        try {
            Organisation org = verificationService.validateToken(token);

            // Set session attributes so the rest of the flow works
            session.setAttribute("ORG_ID", org.getId());

            model.addAttribute("pageTitle", "Resume Document Verification");
            model.addAttribute("orgName", org.getName());
            model.addAttribute("orgId", org.getId());
            model.addAttribute("resumeToken", token);

            return "verification/verify-documents";
        } catch (Exception e) {
            log.error("Invalid verification token: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "error/access-denied";
        }
    }
}
