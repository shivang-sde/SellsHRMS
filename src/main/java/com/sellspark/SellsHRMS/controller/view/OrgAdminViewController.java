package com.sellspark.SellsHRMS.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrgAdminViewController {

    @GetMapping("/orgadmin/dashboard")
    public String dashboard() {
        return "orgadmin/dashboard";
    }

    @GetMapping("/orgadmin/employees")
    public String employees() {
        return "orgadmin/employees";
    }
}
