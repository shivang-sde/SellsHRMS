package com.sellspark.SellsHRMS.notification.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.sellspark.SellsHRMS.notification.enums.TargetRole;

@Controller
public class NotificationTemplateViewController {

    @GetMapping("/superadmin/notifications/templates")
    public String notificationTemplatesPage(Model model) {
        model.addAttribute("pageTitle", "Notification Template Management");
        model.addAttribute("contentPage", "superadmin/notifications/templates");
        model.addAttribute("pageScript", "superadmin/notifications/templates");
        model.addAttribute("pageStyle", "notifications");
        model.addAttribute("targetRoles", TargetRole.values());
        return "layout/main-layout";
    }
}
