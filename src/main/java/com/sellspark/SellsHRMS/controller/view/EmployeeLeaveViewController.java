package com.sellspark.SellsHRMS.controller.view;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
@RequestMapping("/employee/leave/ex")
public class EmployeeLeaveViewController {

    @GetMapping
    public String leavePage(Model model) {
        model.addAttribute("pageTitle", "My Leaves");
        model.addAttribute("contentPage", "employee/leave");
        model.addAttribute("pageScript", "employee/leave");
        return "layout/main-layout";
    }

    @GetMapping("/apply")
    public String applyLeavePage(Model model) {
        model.addAttribute("pageTitle", "Apply Leave");
        model.addAttribute("contentPage", "employee/leave-apply");
        model.addAttribute("pageScript", "employee/leave-apply");
        return "layout/main-layout";
    }
}
