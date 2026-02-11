package com.sellspark.SellsHRMS.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AssetPageController {

    @GetMapping("/org/assets")
    public String showAssets(Model model) {
        model.addAttribute("pageTitle", "Asset Management");
        model.addAttribute("contentPage", "orgadmin/asset/asset-list");
        model.addAttribute("pageScript", "orgadmin/asset-list");
        model.addAttribute("pageStyle", "asset");
        return "layout/main-layout";
    }

    @GetMapping("/org/assets/categories")
    public String showCategories(Model model) {
        model.addAttribute("pageTitle", "Asset Categories");
        model.addAttribute("contentPage", "orgadmin/asset/asset-categories");
        model.addAttribute("pageScript", "orgadmin/asset-categories");
        model.addAttribute("pageStyle", "asset");
        return "layout/main-layout";
    }

    @GetMapping("/org/assets/vendors")
    public String showVendors(Model model) {
        model.addAttribute("pageTitle", "Vendors");
        model.addAttribute("contentPage", "orgadmin/asset/asset-vendors");
        model.addAttribute("pageScript", "orgadmin/asset-vendors");
        model.addAttribute("pageStyle", "asset");
        return "layout/main-layout";
    }

    @GetMapping("/org/assets/maintenance")
    public String showMaintenance(Model model) {
        model.addAttribute("pageTitle", "Asset Maintenance Logs");
        model.addAttribute("contentPage", "orgadmin/asset/asset-maintenance");
        model.addAttribute("pageScript", "orgadmin/asset-maintenance");
        model.addAttribute("pageStyle", "asset");
        return "layout/main-layout";
    }

    @GetMapping("/org/assets/assignments")
    public String showAssignments(Model model) {
        model.addAttribute("pageTitle", "Asset Assignments");
        model.addAttribute("contentPage", "orgadmin/asset/asset-assignments");
        model.addAttribute("pageScript", "orgadmin/asset-assignments");
        model.addAttribute("pageStyle", "asset");
        return "layout/main-layout";
    }
}
