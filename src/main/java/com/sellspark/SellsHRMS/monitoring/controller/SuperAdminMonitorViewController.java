package com.sellspark.SellsHRMS.monitoring.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/superadmin/monitor")
@PreAuthorize("hasAuthority('SUPER_ADMIN')")
public class SuperAdminMonitorViewController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Global URL Monitor Dashboard");
        model.addAttribute("contentPage", "superadmin/monitor/dashboard");
        model.addAttribute("pageScript", "superadmin/monitor/dashboard");
        model.addAttribute("pageStyle", "monitor/monitor");
        return "layout/main-layout";
    }

    @GetMapping("/urls")
    public String urls(Model model) {
        model.addAttribute("pageTitle", "All Monitored URLs");
        model.addAttribute("contentPage", "superadmin/monitor/urls");
        model.addAttribute("pageScript", "superadmin/monitor/urls");
        model.addAttribute("pageStyle", "monitor/monitor");
        return "layout/main-layout";
    }

    @GetMapping("/incidents")
    public String incidents(Model model) {
        model.addAttribute("pageTitle", "All Incidents");
        model.addAttribute("contentPage", "superadmin/monitor/incidents");
        model.addAttribute("pageScript", "superadmin/monitor/incidents");
        model.addAttribute("pageStyle", "monitor/monitor");
        return "layout/main-layout";
    }
}
