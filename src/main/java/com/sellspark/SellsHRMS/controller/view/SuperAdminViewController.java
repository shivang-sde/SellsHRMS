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

    @GetMapping("/superadmin/org")
    public String superAdminHome(Model model) { 
        model.addAttribute("pageTitle", "Super Admin Home");
        model.addAttribute("contentPage", "superadmin/org-dashboard"); // create this JSP
        return "layout/main-layout";
    }



    // LIST ALL ORGANISATIONS
    @GetMapping("/superadmin/organisations")
    public String organisations(Model model) {
        model.addAttribute("pageTitle", "Organisations");
        model.addAttribute("contentPage", "superadmin/organisation-list");
         model.addAttribute("pageScript", "organisation-list");
        return "layout/main-layout";
    }

    // CREATE ORGANISATION (page)
    @GetMapping("/superadmin/create-organisation")
    public String createOrganisationPage(Model model) {
        model.addAttribute("pageTitle", "Create Organisation");
        model.addAttribute("contentPage", "superadmin/organisation-form"); // create this JSP
        
        return "layout/main-layout";
    }

    // EDIT ORGANISATION (page)
    @GetMapping("/superadmin/organisation/edit/{id}")
    public String editOrganisationPage(Model model) {
        model.addAttribute("pageTitle", "Edit Organisation");
        model.addAttribute("contentPage", "superadmin/organisation-form"); // reuse create JSP
        return "layout/main-layout";
    }

    // CREATE ORG ADMIN (page)
    @GetMapping("/superadmin/create-orgadmin")
    public String createOrgAdminPage(Model model) {
        model.addAttribute("pageTitle", "Create Organisation Admin");
        model.addAttribute("contentPage", "superadmin/orgadmin-form"); // create this JSP
        return "layout/main-layout";
    }

    // LIST OF ORG ADMINS
    @GetMapping("/superadmin/orgadmins")
    public String orgAdminsPage(Model model) {
        model.addAttribute("pageTitle", "Organisation Admins");
        model.addAttribute("pageScript", "orgadmin-list");
        model.addAttribute("contentPage", "superadmin/orgadmin-list"); // create this JSP
        return "layout/main-layout";
    }
}
