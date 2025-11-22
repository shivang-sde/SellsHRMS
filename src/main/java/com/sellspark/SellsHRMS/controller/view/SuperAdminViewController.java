package com.sellspark.SellsHRMS.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SuperAdminViewController {

    // SUPER ADMIN DASHBOARD
    @GetMapping("/superadmin/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Super Admin Dashboard");
        model.addAttribute("contentPage", "superadmin/dashboard"); // create this JSP
        return "layout/main-layout";
    }

    // LIST ALL ORGANISATIONS
    @GetMapping("/superadmin/organisations")
    public String organisations(Model model) {
        model.addAttribute("pageTitle", "Organisations");
        model.addAttribute("contentPage", "superadmin/organisations");
        return "layout/main-layout";
    }

    // CREATE ORGANISATION (page)
    @GetMapping("/superadmin/create-organisation")
    public String createOrganisationPage(Model model) {
        model.addAttribute("pageTitle", "Create Organisation");
        model.addAttribute("contentPage", "superadmin/create-organisation"); // create this JSP
        return "layout/main-layout";
    }

    // CREATE ORG ADMIN (page)
    @GetMapping("/superadmin/create-orgadmin")
    public String createOrgAdminPage(Model model) {
        model.addAttribute("pageTitle", "Create Organisation Admin");
        model.addAttribute("contentPage", "superadmin/create-orgadmin"); // create this JSP
        return "layout/main-layout";
    }

    // LIST OF ORG ADMINS
    @GetMapping("/superadmin/orgadmins")
    public String orgAdminsPage(Model model) {
        model.addAttribute("pageTitle", "Organisation Admins");
        model.addAttribute("contentPage", "superadmin/orgadmins");
        return "layout/main-layout";
    }
}
