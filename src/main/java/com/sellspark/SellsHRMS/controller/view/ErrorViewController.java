package com.sellspark.SellsHRMS.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ErrorViewController {

    /** License expired or inactive org **/
    @GetMapping("/error/license-expired")
    public String licenseExpiredPage(@RequestParam(required = false) String code,
                                     @RequestParam(required = false) String msg,
                                     Model model) {
        model.addAttribute("pageTitle", "License Expired");
        model.addAttribute("contentPage", "error/license-expired");
        model.addAttribute("errorCode", code);
        model.addAttribute("errorMessage", msg);
        return "layout/error_base"; // ✅ NEW minimal layout
    }

    /** Generic Access Denied (403) **/
    @GetMapping("/error/403")
    public String accessDeniedPage(Model model) {
        model.addAttribute("pageTitle", "Access Denied");
        model.addAttribute("contentPage", "error/access-denied");
        return "layout/error_base";
    }

    /** Session expired or unauthenticated **/
    @GetMapping("/error/session-expired")
    public String sessionExpiredPage(Model model) {
        model.addAttribute("pageTitle", "Session Expired");
        model.addAttribute("contentPage", "error/session-expired");
        return "layout/error_base";
    }
}
