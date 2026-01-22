package com.sellspark.SellsHRMS.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employee")
public class EmployeeViewController {

    @GetMapping("/dashboard")
    public String employeeDashboard(Model model) {
      
        model.addAttribute("pageTitle", "Employee Dashboard");
        model.addAttribute("contentPage", "employee/dashboard");
        model.addAttribute("pageScript", "employee/employee-dashboard");
        return "layout/main-layout";
    }

    @GetMapping("/profile")
    public String employeeProfile(Model model) {
        
        model.addAttribute("pageTitle", "Employee Profile");
        model.addAttribute("contentPage", "employee/employee-detail");
        model.addAttribute("pageScript", "employee/employee-detail");
        return "layout/main-layout";
    }

    @GetMapping("/salaries")
    public String employeeSalarySlip(Model model) {
        
        model.addAttribute("pageTitle", "Salary Slips");
        model.addAttribute("contentPage", "employee/salary-slips");
        model.addAttribute("pageScript", "employee/salary-slips");
        return "layout/main-layout";
    }

}
