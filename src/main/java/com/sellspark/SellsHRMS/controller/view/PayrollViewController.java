package com.sellspark.SellsHRMS.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

/**
 * Payroll Module View Controller
 * Responsible for rendering JSP pages for Payroll & Tax Management UI.
 * 
 * All views are rendered inside main.jsp (layout),
 * which includes sidebar, header, and dynamic content via contentPage variable.
 */  
@Controller
@RequestMapping("/payroll")
public class PayrollViewController {
    
    @GetMapping("/salary-components")
    public String salaryComponents(Model model) {
        model.addAttribute("pageTitle", "Salary Components");
        model.addAttribute("contentPage", "orgadmin/payroll/salary-components");
        model.addAttribute("pageScript", "payroll/salary-components");
        return "layout/main-layout";
    }

    @GetMapping("/salary-structures")
    public String salaryStructure(Model model) {
        model.addAttribute("pageTitle", "Salary Components");
        model.addAttribute("contentPage", "orgadmin/payroll/salary-structure");
        model.addAttribute("pageScript", "payroll/salary-structure");
        return "layout/main-layout";
    }

    @GetMapping("/salary-assignments")
    public String viewSalaryAssignments(Model model, HttpSession session) {
        setCommonAttributes(model, "orgadmin/payroll/assignments", "Salary Assignments", "payroll/assignments");
        return "layout/main-layout";
    }

    @GetMapping("/statutory-tax")
    public String viewStatutoryTax(Model model, HttpSession session) {
        setCommonAttributes(model, "orgadmin/payroll/statutory-tax", "Statutory & Tax Setup", "payroll/statutory-tax");
        return "layout/main-layout";
    }



//     @GetMapping("/payrun/{id}")
// public String viewPayRunDetails(Model model, @PathVariable Long id) {
//     model.addAttribute("contentPage", "orgadmin/payroll/payrun-details");
//     model.addAttribute("pageTitle", "Pay Run Details");
//     model.addAttribute("pageScript", "payroll/payrun-details");
//     model.addAttribute("payRunId", id);
//     return "layout/main-layout";
// }




@GetMapping("/payslip/{id}")
public String viewPayslip(Model model, @PathVariable Long id) {
    model.addAttribute("pageTitle", "Employee Payslip");
    model.addAttribute("contentPage", "orgadmin/payroll/payslip-view");
    model.addAttribute("pageScript", "payroll/payslip-view");
    model.addAttribute("slipId", id);
    return "layout/main-layout";
}



    @GetMapping("/payruns")
    public String viewPayRunDashboard(Model model, HttpSession session) {
        setCommonAttributes(model, "orgadmin/payroll/payrun-dashboard", "PayRun Dashboard", "payroll/payrun-dashboard");
        return "layout/main-layout";
    }

    @GetMapping("/payrun/{id}")
    public String payrunDetails(Model model, @PathVariable Long id) {
        setCommonAttributes(model, "orgadmin/payroll/payrun-details", "PayRun Details", "payroll/payrun-details");
        model.addAttribute("payRunId", id);
        return "layout/main-layout";
    }

     @GetMapping("/payslips")
    public String viewPayslips(Model model, HttpSession session) {
        setCommonAttributes(model, "orgadmin/payroll/payslips", "Payslips", "payroll/payslips");
        return "layout/main-layout";
    }

    //  @GetMapping("/payslip/{id}")
    // public String payslipView(Model model, @PathVariable Long id) {
    //     setCommonAttributes(model, "orgadmin/payroll/payslip-view", "Employee Payslip", "payroll/payslip-view");
    //     model.addAttribute("slipId", id);
    //     return "layout/main-layout";
    // }

    @GetMapping("/reports")
    public String viewReports(Model model, HttpSession session) {
        setCommonAttributes(model, "orgadmin/payroll/reports", "Payroll Reports", "payroll/reports");
        return "layout/main-layout";
    }


    //   * Helper method to set common model attributes
    private void setCommonAttributes(Model model, String contentPage, String pageTitle, String pageScript) {
        model.addAttribute("contentPage", contentPage);
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("pageScript", pageScript);
        return;
    }
}
