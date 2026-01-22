package com.sellspark.SellsHRMS.controller.view;
import com.sellspark.SellsHRMS.service.payroll.SalarySlipTemplateService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/salary-slip-template")
@RequiredArgsConstructor
public class SalarySlipTemplateViewController {

    private final SalarySlipTemplateService templateService;

    /**
     * Show template designer page
     */
    @GetMapping("/design")
    public String designTemplate(Model model, HttpSession session) {
        Long orgId = (Long) session.getAttribute("ORG_ID");
        if (orgId == null) {
            return "redirect:/login";
        }

        model.addAttribute("pageTitle", "Salary Slip Template Designer");
        model.addAttribute("contentPage", "orgadmin/payroll/template-designer");
        model.addAttribute("pageScript", "payroll/template-designer");
        return "layout/main-layout";
    }

    /**
     * Show template list page
     */
    @GetMapping("/list")
    public String listTemplates(Model model, HttpSession session) {
        Long orgId = (Long) session.getAttribute("ORG_ID");
        if (orgId == null) {
            return "redirect:/login";
        }

        model.addAttribute("pageTitle", "Salary Slip Templates");
        model.addAttribute("contentPage", "orgadmin/payroll/template-list");
        model.addAttribute("pageScript", "payroll/template-list");
        return "layout/main-layout";
    }

    /**
     * Edit existing template
     */
    @GetMapping("/edit/{id}")
    public String editTemplate(@PathVariable Long id, Model model, HttpSession session) {
        Long orgId = (Long) session.getAttribute("ORG_ID");
        if (orgId == null) {
            return "redirect:/login";
        }

        model.addAttribute("templateId", id);
        model.addAttribute("pageTitle", "Edit Salary Slip Template");
        model.addAttribute("contentPage", "orgadmin/payroll/template-designer");
        model.addAttribute("pageScript", "payroll/template-designer");
        return "layout/main-layout";
    }

    /**
     * Preview salary slip for an employee
     */
    @GetMapping("/preview/{salarySlipId}")
    public String previewSalarySlip(@PathVariable Long salarySlipId, Model model, HttpSession session) {
        Long orgId = (Long) session.getAttribute("ORG_ID");
        if (orgId == null) {
            return "redirect:/login";
        }

        try {
            String html = templateService.renderSalarySlip(salarySlipId, orgId);
            model.addAttribute("slipHtml", html);
            model.addAttribute("salarySlipId", salarySlipId);
            model.addAttribute("pageTitle", "Salary Slip Preview");
            model.addAttribute("contentPage", "orgadmin/payroll/slip-preview");
            model.addAttribute("pageScript", "payroll/slip-preview");
            return "layout/main-layout";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}