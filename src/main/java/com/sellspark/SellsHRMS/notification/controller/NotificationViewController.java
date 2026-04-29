package com.sellspark.SellsHRMS.notification.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.sellspark.SellsHRMS.notification.enums.TargetRole;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class NotificationViewController {

    // ============ ORG LEVEL PAGES ============

    @GetMapping("/org/notifications/smtp-settings")
    public String smtpSettingsPage(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "SMTP Settings");
        model.addAttribute("contentPage", "orgadmin/notifications/smtp-settings");
        model.addAttribute("pageScript", "orgadmin/notifications/smtp-config");
        model.addAttribute("pageStyle", "notifications");

        return "layout/main-layout";
    }

    @GetMapping("/org/notifications/preferences")
    public String preferencesPage(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Notification Preferences");
        model.addAttribute("contentPage", "orgadmin/notifications/preferences");
        model.addAttribute("pageScript", "orgadmin/notifications/preferences");
        model.addAttribute("pageStyle", "notifications");
        model.addAttribute("targetRoles", TargetRole.values());

        return "layout/main-layout";
    }

    // ============ SUPERADMIN LEVEL PAGES ============

    @GetMapping("/superadmin/notifications/events")
    public String eventsPage(Model model) {
        model.addAttribute("pageTitle", "Notification Events");
        model.addAttribute("contentPage", "superadmin/notifications/events");
        model.addAttribute("pageScript", "superadmin/notifications/events");
        model.addAttribute("pageStyle", "notifications");

        return "layout/main-layout";
    }
}