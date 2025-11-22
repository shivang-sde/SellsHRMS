package com.sellspark.SellsHRMS.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmployeePageController {

    @GetMapping("/user/login")
    public String login() {
        return "login";
    }

    @GetMapping("/user/dashboard")
    public String dashboard() {
        return "user/dashboard";
    }
}
