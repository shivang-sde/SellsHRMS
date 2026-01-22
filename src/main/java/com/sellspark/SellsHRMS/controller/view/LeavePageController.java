package com.sellspark.SellsHRMS.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LeavePageController {

    // ====================================
    // Employee Leave Pages
    // ====================================

    @GetMapping("/employee/leave")
    public String employeeLeave(Model model) {
        model.addAttribute("pageTitle", "My Leaves");
        model.addAttribute("contentPage", "employee/leave");
        model.addAttribute("pageScript", "employee/leave");
        return "layout/main-layout";
    }

    // ====================================
    // Org Admin Leave Pages
    // ====================================

    @GetMapping("/org/leaves")
    public String orgLeaves(Model model) {
        model.addAttribute("pageTitle", "Leave Management");
        model.addAttribute("contentPage", "orgadmin/leaves");
        model.addAttribute("pageScript", "orgadmin/leaves");
        return "layout/main-layout";
    }

    @GetMapping("/org/leave-types")
    public String orgLeaveTypes(Model model) {
        model.addAttribute("pageTitle", "Leave Types");
        model.addAttribute("contentPage", "orgadmin/leave-types");
        model.addAttribute("pageScript", "orgadmin/leave-types");
        return "layout/main-layout";
    }

    @GetMapping("/org/leave-balances")
    public String orgLeaveBalances(Model model) {
        model.addAttribute("pageTitle", "Leave Balances");
        model.addAttribute("contentPage", "orgadmin/leave-balances");
        model.addAttribute("pageScript", "orgadmin/leave-balances");
        return "layout/main-layout";
    }
}