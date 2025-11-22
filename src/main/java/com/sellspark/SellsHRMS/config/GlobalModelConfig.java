package com.sellspark.SellsHRMS.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Controller
public class GlobalModelConfig {

    @ModelAttribute("role")
    public String role(HttpSession session) {
        Object r = session.getAttribute("SYSTEM_ROLE");
        return r == null ? null : r.toString();
    }

    @ModelAttribute("modules")
    public List<String> modules(HttpSession session) {
        Object m = session.getAttribute("MODULES");
        if (m instanceof List)
            return (List<String>) m;
        return Collections.emptyList();
    }

    @ModelAttribute("permissions")
    public Set<String> permissions(HttpSession session) {
        Object p = session.getAttribute("PERMISSIONS");
        if (p instanceof Set)
            return (Set<String>) p;
        return Set.of();
    }

    @ModelAttribute("email")
    public String email(HttpSession session) {
        Object v = session.getAttribute("EMAIL");
        return v == null ? null : v.toString();
    }
}
