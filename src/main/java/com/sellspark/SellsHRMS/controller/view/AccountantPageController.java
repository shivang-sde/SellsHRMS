package com.sellspark.SellsHRMS.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.time.LocalDate;

@Controller
public class AccountantPageController {

    @GetMapping("/accountant-panel")
    public String showAccountantPanel(Model model) {
        LocalDate now = LocalDate.now();
        model.addAttribute("currentMonth", now.getMonthValue());
        model.addAttribute("currentYear", now.getYear());
        model.addAttribute("pageTitle", "Accountant Dashboard");
        model.addAttribute("contentPage", "accountant/accountant-panel");
        model.addAttribute("pageScript", "accountant/accountant-panel");
        model.addAttribute("pageStyle", "accountant-panel");
        return "layout/main-layout";
    }

    @GetMapping("/accountant/user")
    public String showAccountantUser(Model model) {
        model.addAttribute("pageTitle", "Add Accountant");
        model.addAttribute("contentPage", "accountant/user");
        model.addAttribute("pageScript", "accountant/user");
        return "layout/main-layout";
    }
}
