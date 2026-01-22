package com.sellspark.SellsHRMS.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/employee/attendance")
public class EmployeeAttendanceViewController {

    @GetMapping
    public String attendancePage(Model model) {
        model.addAttribute("pageTitle", "My Attendance");
        model.addAttribute("contentPage", "employee/attendance");
        model.addAttribute("pageScript", "employee/attendance");
        return "layout/main-layout";
    }
}
