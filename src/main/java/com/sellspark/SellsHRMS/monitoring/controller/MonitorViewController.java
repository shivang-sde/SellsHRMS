package com.sellspark.SellsHRMS.monitoring.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/org/monitor")
public class MonitorViewController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "URL Monitor Dashboard");
        model.addAttribute("contentPage", "monitor/dashboard");
        model.addAttribute("pageScript", "monitor/dashboard");
        model.addAttribute("pageStyle", "monitor/monitor");
        return "layout/main-layout";
    }

    @GetMapping("/urls")
    public String urls(Model model) {
        model.addAttribute("pageTitle", "Monitor URLs");
        model.addAttribute("contentPage", "monitor/urls");
        model.addAttribute("pageScript", "monitor/urls");
        model.addAttribute("pageStyle", "monitor/monitor");
        return "layout/main-layout";
    }

    @GetMapping("/groups")
    public String groups(Model model) {
        model.addAttribute("pageTitle", "Monitor Groups");
        model.addAttribute("contentPage", "monitor/groups");
        model.addAttribute("pageScript", "monitor/groups");
        model.addAttribute("pageStyle", "monitor/monitor");
        return "layout/main-layout";
    }

    @GetMapping("/incidents")
    public String incidents(Model model) {
        model.addAttribute("pageTitle", "Incident History");
        model.addAttribute("contentPage", "monitor/incidents");
        model.addAttribute("pageScript", "monitor/incidents");
        model.addAttribute("pageStyle", "monitor/monitor");
        return "layout/main-layout";
    }
}
