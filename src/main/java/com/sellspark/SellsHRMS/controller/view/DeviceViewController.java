package com.sellspark.SellsHRMS.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/org/devices")
public class DeviceViewController {

    @GetMapping
    public String devicesPage(Model model, @RequestParam(required = false) Long orgId) {
        model.addAttribute("pageTitle", "Device Management");
        model.addAttribute("contentPage", "orgadmin/devices");
        model.addAttribute("pageScript", "orgadmin/devices");

        if (orgId != null) {
            model.addAttribute("orgId", orgId);
        }

        return "layout/main-layout";
    }
}
