package com.sellspark.SellsHRMS.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;


@Controller
@RequestMapping("/org")
@Slf4j
public class OrgAdminViewController {



    @GetMapping("/dashboard")
    public String orgDashboardPage(Model model) {
        model.addAttribute("pageTitle", "OrgAdmin Dashboard");
        model.addAttribute("contentPage", "orgadmin/dashboard");
        model.addAttribute("pageScript", "orgadmin-dashboard");
           return "layout/main-layout";   
    }


    
    /**
     * Attendance Dashboard Page
     * GET /orgadmin/attendance-dashboard
     */
    @GetMapping("/attendance-analytics-dashboard")
    public String attendanceDashboard(Model model, HttpSession session) {
        log.info("Loading attendance dashboard for organisation: {}", session.getAttribute("ORG_ID"));
        
        // Set page metadata
        model.addAttribute("pageTitle", "Attendance Analytics Dashboard");
        model.addAttribute("contentPage", "orgadmin/analytics/attendance-analytics-dashboard");
        model.addAttribute("pageScript", "orgadmin/analytics/attendance-analytics-dashboard");
        
        // Return main layout which will include the content
        return "layout/main-layout";
    }


    
    /**
     * Reports Page
     * GET /orgadmin/reports
     */
    @GetMapping("/reports")
    public String reports(Model model, HttpSession session) {
        log.info("Loading reports page for organisation: {}", session.getAttribute("ORG_ID"));
        
        model.addAttribute("pageTitle", "Reports");
        model.addAttribute("contentPage", "orgadmin/reports");
        model.addAttribute("pageScript", "reports");
        
        return "layout/main-layout";
    }

    /**
     * Analytics Page
     * GET /orgadmin/analytics
     */
    @GetMapping("/analytics")
    public String analytics(Model model, HttpSession session) {
        log.info("Loading analytics page for organisation: {}", session.getAttribute("ORG_ID"));
        
        model.addAttribute("pageTitle", "Analytics");
        model.addAttribute("contentPage", "orgadmin/analytics");
        model.addAttribute("pageScript", "analytics");
        
        return "layout/main-layout";
    }

    @GetMapping("/employees")
    public String employeeListPage(Model model) {
        model.addAttribute("pageTitle", "Employees");
        model.addAttribute("contentPage", "orgadmin/employee-list");
        return "layout/main-layout";
    }

    @GetMapping("/create-employee")
    public String createEmployeePage(Model model) {
        model.addAttribute("pageTitle", "Create Employee");
        model.addAttribute("contentPage", "orgadmin/employee-form");
        model.addAttribute("pageScript", "employee/employee-form"); // matches /js/employee-form.js
        return "layout/main-layout";
    }

    @GetMapping("/employee/edit/{id}")
    public String editEmployeePage(@PathVariable Long id, Model model) {
    model.addAttribute("employeeId", id);
    model.addAttribute("pageTitle", "Edit Employee");
    model.addAttribute("contentPage", "orgadmin/employee-form"); // same JSP!
    model.addAttribute("pageScript", "employee/employee-form");
    return "layout/main-layout";
    }

    @GetMapping("/departments")
    public String deptPage(Model model) {
        model.addAttribute("pageTitle", "Departments");
        model.addAttribute("contentPage", "orgadmin/department");
        model.addAttribute("pageScript", "dept"); // matches /js/dept.js
        return "layout/main-layout";
    }

     @GetMapping("/designations")
    public String desgPage(Model model) {
        model.addAttribute("pageTitle", "Designations");
        model.addAttribute("contentPage", "orgadmin/designation");
        model.addAttribute("pageScript", "desig"); // matches /js/sdesg.js
        return "layout/main-layout";
    }


    @GetMapping("/organisation-policy")
    public String organisationPolicyPage(Model model) {
        model.addAttribute("pageTitle", "Organisation Policy");
        model.addAttribute("contentPage", "orgadmin/organisation-policy");
        model.addAttribute("pageScript", "orgadmin/organisation-policy");
        return "layout/main-layout";
    }


    @GetMapping("/employee/{id}")
    public String employeeDetailPage(@PathVariable Long id, Model model) {
        model.addAttribute("employeeId", id);
        model.addAttribute("pageTitle", "Employee Details");
        model.addAttribute("contentPage", "orgadmin/employee-detail");
        model.addAttribute("pageScript", "employee/employee-detail");
        return "layout/main-layout";
    }

    // Attendance

    @GetMapping("/attendance")
public String attendancePage(Model model) {
    model.addAttribute("pageTitle", "Today's Attendance");
    model.addAttribute("contentPage", "orgadmin/attendance");
    model.addAttribute("pageScript", "orgadmin/attendance");
    return "layout/main-layout";
}

@GetMapping("/attendance/reports")
public String attendanceReportsPage(Model model) {
    model.addAttribute("pageTitle", "Attendance Reports");
    model.addAttribute("contentPage", "orgadmin/attendance-reports");
    model.addAttribute("pageScript", "orgadmin/attendance-reports");
    return "layout/main-layout";
}


@GetMapping("/holidays")
public String holidaysPage(Model model) {
    model.addAttribute("pageTitle", "Holiday Management");
    model.addAttribute("contentPage", "orgadmin/holidays");
    model.addAttribute("pageScript", "orgadmin/holidays");
    return "layout/main-layout";
}

// // Leave Management
// @GetMapping("/leaves/types")
// public String leaveTypesPage(Model model) {
//     model.addAttribute("pageTitle", "Leave Types");
//     model.addAttribute("contentPage", "orgadmin/leave-types");
//     model.addAttribute("pageScript", "orgadmin/leave-types");
//     return "layout/main-layout";
// }

// @GetMapping("/leaves/pending")
// public String pendingLeavesPage(Model model) {
//     model.addAttribute("pageTitle", "Pending Leave Approvals");
//     model.addAttribute("contentPage", "orgadmin/leave-approvals");
//     model.addAttribute("pageScript", "orgadmin/leave-approvals");
//     return "layout/main-layout";
// }

// @GetMapping("/leaves")
// public String allLeavesPage(Model model) {
//     model.addAttribute("pageTitle", "All Leaves");
//     model.addAttribute("contentPage", "orgadmin/leave-management");
//     model.addAttribute("pageScript", "orgadmin/leave-management");
//     return "layout/main-layout";
// }

// @GetMapping("/leaves/reports")
// public String leaveReportsPage(Model model) {
//     model.addAttribute("pageTitle", "Leave Reports");
//     model.addAttribute("contentPage", "orgadmin/leave-reports");
//     model.addAttribute("pageScript", "orgadmin/leave-reports");
//     return "layout/main-layout";
// }

     @GetMapping("/roles")
    public String rolesPage(Model model) {
        model.addAttribute("pageTitle", "Roles");
        model.addAttribute("contentPage", "orgadmin/roles");
        model.addAttribute("pageScript", "orgadmin/roles");
        return "layout/main-layout";
    }

    @GetMapping("/create-role")
    public String createRolePage(Model model) {
        model.addAttribute("pageTitle", "Create Role");
        model.addAttribute("contentPage", "orgadmin/create-role");
        model.addAttribute("pageScript", "orgadmin/create-role");
        return "layout/main-layout";
    }

}

