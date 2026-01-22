package com.sellspark.SellsHRMS.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrgInfoPageController {

    // ====================================
    // Knowledge Base Pages
    // ====================================

    @GetMapping("/org/knowledge-base")
    public String orgKnowledgeBase(Model model) {
        model.addAttribute("pageTitle", "Knowledge Base");
        model.addAttribute("contentPage", "orgadmin/knowledge-base");
        model.addAttribute("pageScript", "orgadmin/knowledge-base");
        return "layout/main-layout";
    }

    @GetMapping("/employee/knowledge-base")
    public String employeeKnowledgeBase(Model model) {
        model.addAttribute("pageTitle", "Knowledge Base");
        model.addAttribute("contentPage", "employee/knowledge-base");
        model.addAttribute("pageScript", "employee/knowledge-base");
        return "layout/main-layout";
    }

    // ====================================
    // Announcement Pages
    // ====================================

    @GetMapping("/org/announcements")
    public String orgAnnouncements(Model model) {
        model.addAttribute("pageTitle", "Announcements");
        model.addAttribute("contentPage", "orgadmin/announcements");
        model.addAttribute("pageScript", "orgadmin/announcements");
        return "layout/main-layout";
    }

    @GetMapping("/employee/announcements")
    public String employeeAnnouncements(Model model) {
        model.addAttribute("pageTitle", "Announcements");
        model.addAttribute("contentPage", "employee/announcements");
        model.addAttribute("pageScript", "employee/announcements");
        return "layout/main-layout";
    }

    // ====================================
    // Event Pages
    // ====================================

    @GetMapping("/org/events")
    public String orgEvents(Model model) {
        model.addAttribute("pageTitle", "Events");
        model.addAttribute("contentPage", "orgadmin/events");
        model.addAttribute("pageScript", "orgadmin/events");
        return "layout/main-layout";
    }

    @GetMapping("/employee/events")
    public String employeeEvents(Model model) {
        model.addAttribute("pageTitle", "Events");
        model.addAttribute("contentPage", "employee/events");
        model.addAttribute("pageScript", "employee/events");
        return "layout/main-layout";
    }
}